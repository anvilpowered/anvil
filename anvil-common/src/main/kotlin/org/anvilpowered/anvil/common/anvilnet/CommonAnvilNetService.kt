/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
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
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.anvilnet.AnvilNetService
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
import java.util.concurrent.ConcurrentHashMap
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

  private lateinit var listeners:
    Table<Int, KClass<out AnvilNetPacket>, MutableMap<((AnvilNetPacket) -> Boolean)?, MutableList<(AnvilNetPacket) -> Unit>>>

  private lateinit var blockingListeners:
    Table<Int, KClass<out AnvilNetPacket>, MutableMap<((AnvilNetPacket) -> Boolean)?, MutableList<(AnvilNetPacket) -> Unit>>>

  private fun runListeners(
    packet: AnvilNetPacket,
    nodeId: Int,
    removeIfRan: Boolean,
    listeners: Table<Int, KClass<out AnvilNetPacket>, MutableMap<((AnvilNetPacket) -> Boolean)?, MutableList<(AnvilNetPacket) -> Unit>>>,
  ) {
    val foo = listeners[nodeId, packet::class]
    val it = (foo ?: return).iterator()
    while (it.hasNext()) {
      val next = it.next()
      val pred = next.key
      if (pred == null || pred(packet)) {
        for (listener in next.value) {
          listener(packet)
        }
        if (removeIfRan) {
          it.remove()
        }
      }
    }
  }

  private fun primaryListener(packet: AnvilNetPacket) {
    requireNotNull(packet.header) { "packet header" }
    val nodeId = packet.header.path.sourceId
    runListeners(packet, 0, true, blockingListeners)
    runListeners(packet, nodeId, true, blockingListeners)
    runListeners(packet, 0, false, listeners)
    runListeners(packet, nodeId, false, listeners)
  }

  companion object {
    const val MESSAGE_DELIMITER = ";"

    /**
     * Shared instance of packet predicate
     */
    val truePred: (AnvilNetPacket) -> Boolean = { true }
  }

  init {
    registry.whenLoaded(::registryLoaded).order(-5).register()
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
    communicator.setListener(nodeId, ::primaryListener)
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
        try {
          val packetType = packetTranslator.fromPacketType(packet::class)
          packet.prepare(registry.getOrDefault(Keys.SERVER_NAME))
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

  inner class RegisterEnd<T : AnvilNetPacket>(
    private val packetType: KClass<T>,
    private val listener: (T) -> Unit,
  ) {
    private var nodeId: Int = 0
    private var blocking: Boolean = false
    private var timeoutFuture: CompletableFuture<*>? = null
    private var predicate: ((T) -> Boolean)? = null

    fun nodeId(nodeId: Int): RegisterEnd<T> {
      this.nodeId = nodeId
      return this
    }

    fun blocking(timeoutFuture: CompletableFuture<*>? = null): RegisterEnd<T> {
      this.timeoutFuture = timeoutFuture
      this.blocking = true
      return this
    }

    fun predicate(predicate: ((T) -> Boolean)?): RegisterEnd<T> {
      this.predicate = predicate
      return this
    }

    fun register() {
      val pair = Pair(predicate as? (AnvilNetPacket) -> Boolean ?: truePred, listener as (AnvilNetPacket) -> Unit)
      (if (blocking) blockingListeners else listeners).row(nodeId)
        .computeIfAbsent(packetType) { ConcurrentHashMap() }
        .computeIfAbsent(pair.first) { mutableListOf() }
        .add(pair.second)
      timeoutFuture?.thenAccept { blockingListeners.row(nodeId)[packetType]?.remove(pair.first, pair.second) }
    }
  }

  inline fun <reified T : AnvilNetPacket> prepareNext(noinline predicate: ((T) -> Boolean)? = null): NextPacketEnd<T> {
    return NextPacketEnd(T::class, predicate)
  }

  inner class NextPacketEnd<T : AnvilNetPacket>(
    private val packetType: KClass<T>,
    private val predicate: ((T) -> Boolean)? = null,
  ) {
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
      val timeoutFuture: CompletableFuture<T?> = CompletableFuture.supplyAsync {
        try {
          Thread.sleep(timeout)
        } catch (e: InterruptedException) {
          logger.error("AnvilNetPacket waiting thread interrupted", e)
        }
        null
      }
      RegisterEnd(packetType) {
        timeoutFuture.complete(it)
      }.blocking(timeoutFuture).predicate(predicate).register()
      return timeoutFuture
    }
  }

  override fun isConnected(): Boolean {
    return connected
  }
}
