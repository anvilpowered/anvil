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

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
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
import kotlin.reflect.KClass

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

  private lateinit var listeners: Table<Int, KClass<out AnvilNetPacket>, MutableList<(AnvilNetPacket) -> Unit>>

  private lateinit var blockingListeners: Table<Int, KClass<out AnvilNetPacket>, MutableList<(AnvilNetPacket) -> Unit>>

  private val primaryListener = { packet: AnvilNetPacket ->
    requireNotNull(packet.header) { "packet header" }
    val nodeId = packet.header.path.sourceId
    val packetType = packetTranslator.toPacketType(packet.header.type)
    for (listener in blockingListeners[0, packetType]) listener(packet)
    for (listener in blockingListeners[nodeId, packetType]) listener(packet)
    for (listener in listeners[0, packetType]) listener(packet)
    for (listener in listeners[nodeId, packetType]) listener(packet)
  }

  companion object {
    const val MESSAGE_DELIMITER = ";"
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
    listeners = HashBasedTable.create()
    blockingListeners = HashBasedTable.create()
    baseListenersFactory.construct(this).registerAll()
    val nodeId = Random().nextInt()
    val nodeName = registry.getOrDefault(Keys.SERVER_NAME)
    broadcastNetwork.initCurrentNode(nodeId, nodeName)
    pluginMessageNetwork.initCurrentNode(nodeId, nodeName)
    communicator.setListener(nodeId, primaryListener)
    alreadyLoaded = true
  }

  fun prepareSend(packet: AnvilNetPacket): CommonSendEnd {
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
        val packetType = packetTranslator.fromPacketType(packet::class)
        packet.prepare(registry.getOrDefault(Keys.SERVER_NAME))
        logger.info("[Send] $packet")
        try {
          return@supplyAsync (if (connectionType == null) {
            this@CommonAnvilNetService.communicator
          } else when (connectionType!!) {
            ConnectionType.HUB -> broadcastCommunicator
            ConnectionType.VERTICAL -> pluginMessageCommunicator
          }).send(target, packetType, packet)
        } catch (e: Exception) {
          logger.error("An error occurred while sending packet $packet", e)
          return@supplyAsync false
        }
      }
    }
  }

  inline fun <reified T : AnvilNetPacket> prepareRegister(noinline listener: (T) -> Unit): RegisterEnd<T> {
    return RegisterEnd(T::class, listener)
  }

  inner class RegisterEnd<T : AnvilNetPacket>(private val packetType: KClass<T>, private val listener: (T) -> Unit) {
    private var nodeId: Int = 0
    private var blocking: Boolean = false

    fun nodeId(nodeId: Int): RegisterEnd<T> {
      this.nodeId = nodeId
      return this
    }

    fun blocking(): RegisterEnd<T> {
      this.blocking = true
      return this
    }

    fun register() {
      (if (blocking) blockingListeners else listeners).row(nodeId)
        .computeIfAbsent(packetType) { mutableListOf() }
        .add(listener as (AnvilNetPacket) -> Unit)
    }
  }

  inline fun <reified T : AnvilNetPacket> prepareNext(): NextPacketEnd<T> {
    return NextPacketEnd(T::class)
  }

  inner class NextPacketEnd<T : AnvilNetPacket>(private val packetType: KClass<T>) {
    private var nodeId: Int = 0
    private var timeout: Long = 3000

    fun nodeId(nodeId: Int): NextPacketEnd<T> {
      this.nodeId = nodeId
      return this
    }

    fun timeout(timeout: Long): NextPacketEnd<T> {
      this.timeout = timeout
      return this
    }

    fun run(): CompletableFuture<T?> {
      val completableFuture: CompletableFuture<T?> = CompletableFuture.supplyAsync {
        try {
          Thread.sleep(timeout)
        } catch (e: InterruptedException) {
          logger.error("AnvilNetPacket waiting thread interrupted", e)
        }
        null
      }
      RegisterEnd(packetType, completableFuture::complete).blocking().register()
      return completableFuture
    }
  }

  override fun isConnected(): Boolean {
    return connected
  }
}
