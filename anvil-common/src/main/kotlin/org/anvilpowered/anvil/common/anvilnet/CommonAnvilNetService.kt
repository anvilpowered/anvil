/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.common.anvilnet

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.anvilnet.AnvilNetService
import org.anvilpowered.anvil.api.event.Cancellable
import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.api.event.network.ClientConnectionEvent
import org.anvilpowered.anvil.api.event.network.ClientConnectionEvent.Auth.InnerAuth
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.common.anvilnet.communicator.AnvilNetCommunicator
import org.anvilpowered.anvil.common.anvilnet.communicator.CommonPacketTranslator
import org.anvilpowered.anvil.common.anvilnet.communicator.PluginMessageCommunicator
import org.anvilpowered.anvil.common.anvilnet.communicator.RedisCommunicator
import org.anvilpowered.anvil.common.anvilnet.listener.BaseListeners
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import org.anvilpowered.anvil.common.anvilnet.packet.AnvilNetPacket
import org.anvilpowered.anvil.common.misc.InternalInjectorOnly
import org.slf4j.Logger
import java.util.Random
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeoutException
import java.util.function.Consumer

@Suppress("UnstableApiUsage")
@InternalInjectorOnly
@Singleton
class CommonAnvilNetService @Inject constructor(private val registry: Registry) : AnvilNetService {

    @Inject
    private lateinit var baseListenersFactory: BaseListeners.Factory

    @Inject
    private lateinit var logger: Logger

    @Inject
    private lateinit var packetTranslator: CommonPacketTranslator

    @Inject
    private lateinit var communicator: AnvilNetCommunicator

    @Inject
    private lateinit var broadcastCommunicator: RedisCommunicator

    @Inject
    private lateinit var pluginMessageCommunicator: PluginMessageCommunicator

    @Inject
    private lateinit var broadcastNetwork: BroadcastNetwork

    @Inject
    private lateinit var pluginMessageNetwork: PluginMessageNetwork

    private val connected: Boolean
    private val gson: Gson

    private lateinit var listeners: Multimap<Int, Consumer<out AnvilNetPacket>>

    private lateinit var blockingListeners: Multimap<Int, Consumer<out AnvilNetPacket>>

    private val primaryListener = { packet: AnvilNetPacket ->
        val type = packet.header!!.type
        // use raw type for listener; packet should be the right subtype
        for (listener in blockingListeners[type]) {
            (listener as Consumer<AnvilNetPacket>).accept(packet)
        }
        blockingListeners.removeAll(type)
        for (listener in listeners[type]) {
            (listener as Consumer<AnvilNetPacket>).accept(packet)
        }
    }

    companion object {
        const val MESSAGE_DELIMITER = ";"
        const val TIMEOUT_MILLIS = 5000
    }

    init {
        registry.whenLoaded(::registryLoaded).order(-5).register()
        gson = GsonBuilder()
            .registerTypeAdapterFactory(
                RuntimeTypeAdapterFactory.of(Event::class.java, "className")
                    .registerSubtype(
                        Cancellable::class.java,
                        "Cancellable"
                    )
                    .registerSubtype(
                        ClientConnectionEvent::class.java,
                        "ClientConnectionEvent"
                    )
                    .registerSubtype(
                        ClientConnectionEvent.Auth::class.java,
                        "ClientConnectionEvent.Auth"
                    )
                    .registerSubtype(
                        InnerAuth::class.java,
                        "ClientConnectionEvent.Auth.InnerAuth"
                    )
                    .registerSubtype(
                        ClientConnectionEvent.Login::class.java,
                        "ClientConnectionEvent.Login"
                    )
                    .registerSubtype(
                        ClientConnectionEvent.Disconnect::class.java,
                        "ClientConnectionEvent.Disconnect"
                    )
            )
            .create()
        connected = false
    }

    private var alreadyLoaded = false
    private fun registryLoaded() {
        if (alreadyLoaded) {
            return
        }
        listeners = ArrayListMultimap.create()
        blockingListeners = ArrayListMultimap.create()
        baseListenersFactory.construct(this).registerAll()
        val nodeId = Random().nextInt()
        val nodeName = registry.getOrDefault(Keys.SERVER_NAME)
        broadcastNetwork.initCurrentNode(nodeId, nodeName)
        pluginMessageNetwork.initCurrentNode(nodeId, nodeName)
        communicator.setListener(nodeId, primaryListener)
        alreadyLoaded = true
    }

    fun onPlayerLeave() {}
    fun prepare(packet: AnvilNetPacket): CommonSendEnd {
        return CommonSendEnd(packet)
    }

    inner class CommonSendEnd(private val packet: AnvilNetPacket) {
        private var target: Int = 0
        private var connectionType: ConnectionType? = null

        fun target(target: Int): CommonSendEnd {
            this.target = target
            return this
        }

        fun connectionType(connectionType: ConnectionType?): CommonSendEnd {
            this.connectionType = connectionType
            return this
        }

        fun send(): CompletableFuture<Boolean> {
            return CompletableFuture.supplyAsync {
                val packetType = packetTranslator.getPacketType(packet.javaClass)
                packet.prepare(registry.getOrDefault(Keys.SERVER_NAME))
                logger.info("[Send] $packet")
                try {
                    return@supplyAsync (if (connectionType == null) {
                        this@CommonAnvilNetService.communicator
                    } else when (connectionType!!) {
                        ConnectionType.HUB -> broadcastCommunicator
                        ConnectionType.LATERAL -> pluginMessageCommunicator
                    }).send(target, packetType, packet)
                } catch (e: Exception) {
                    logger.error("An error occurred while sending packet $packet", e)
                    return@supplyAsync false
                }
            }
        }
    }

    private fun <T : AnvilNetPacket> register(
        listenerMap: Multimap<Int, Consumer<out AnvilNetPacket>>,
        packetClass: Class<T>,
        listener: Consumer<T>,
    ) {
        listenerMap.put(packetTranslator.getPacketType(packetClass), listener)
    }

    fun <T : AnvilNetPacket> register(clazz: Class<T>, listener: Consumer<T>) {
        register(listeners, clazz, listener)
    }

    fun <T : AnvilNetPacket> nextPacketFuture(clazz: Class<T>, timeout: Long): CompletableFuture<T?> {
        val completableFuture: CompletableFuture<T?> = CompletableFuture.supplyAsync {
            try {
                Thread.sleep(timeout)
            } catch (e: InterruptedException) {
                logger.error("AnvilNetPacket waiting thread interrupted", e)
            }
            null
        }
        register(blockingListeners, clazz) { value: T -> completableFuture.complete(value) }
        return completableFuture
    }

    fun <T : AnvilNetPacket> nextPacketFuture(clazz: Class<T>): CompletableFuture<T?> {
        return nextPacketFuture(clazz, TIMEOUT_MILLIS.toLong())
    }

    @Throws(TimeoutException::class)
    fun <T : AnvilNetPacket> nextPacket(
        clazz: Class<T>, timeout: Long
    ): T {
        return nextPacketFuture(clazz, timeout).join()
            ?: throw TimeoutException("AnvilNetPacket timeout of $timeout exceeded!")
    }

    @Throws(TimeoutException::class)
    fun <T : AnvilNetPacket> nextPacket(clazz: Class<T>): T {
        return nextPacket(clazz, TIMEOUT_MILLIS.toLong())
    }

    override fun isConnected(): Boolean {
        return connected
    }
}
