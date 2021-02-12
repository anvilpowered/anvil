/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.common.event

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.google.common.reflect.TypeToken
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Singleton
import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.api.event.EventListener
import org.anvilpowered.anvil.api.event.EventManager
import org.anvilpowered.anvil.api.event.Listener
import org.anvilpowered.anvil.api.event.Order
import org.anvilpowered.anvil.api.event.PostEventResult
import org.anvilpowered.anvil.api.misc.BindingExtensions
import org.anvilpowered.anvil.common.anvilnet.CommonAnvilNetService
import org.anvilpowered.anvil.common.anvilnet.CommonPacketBus
import org.anvilpowered.anvil.common.anvilnet.communicator.CommonPacketTranslator
import org.anvilpowered.anvil.common.anvilnet.communicator.node.NodeRef
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import org.anvilpowered.anvil.common.anvilnet.packet.EventPostPacket
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

@Singleton
class CommonEventManager @Inject constructor() : EventManager {

  @Inject
  private lateinit var anvilNetService: CommonAnvilNetService

  @Inject
  private lateinit var injector: Injector

  @Inject
  private lateinit var packetBus: CommonPacketBus

  @Inject
  private lateinit var packetTranslator: CommonPacketTranslator

  @Inject
  private lateinit var pluginMessageNetwork: PluginMessageNetwork

  private val localListeners: Table<Order, KClass<out Event>, MutableCollection<EventListener<*>>>

  init {
    localListeners = HashBasedTable.create()
  }

  private fun parseListener(clazz: KClass<out Any>, supplier: () -> Any) {
    for (method in clazz.members) {
      val annotation = method.findAnnotation<Listener>() ?: continue
      val parameters = method.parameters
      check(parameters.size == 1) { "Method annotated with @Listener must have exactly one parameter!" }
      val eventType = parameters[0].type.jvmErasure
      check(eventType.isSubclassOf(Event::class)) { "First parameter of listener must be an event type!" }
      method.isAccessible = true
      localListeners.row(annotation.order)
        .computeIfAbsent(eventType as KClass<out Event>) { ArrayList() }
        .add(MethodEventListener(method, { _, e: Event -> arrayOf(e) }, supplier))
    }
  }

  override fun register(listener: Any) = parseListener(listener::class) { listener }
  override fun register(type: Class<*>) = parseListener(type.kotlin) { injector.getInstance(type) }
  override fun register(key: Key<*>) = parseListener(key.typeLiteral.rawType.kotlin) { injector.getInstance(key) }
  override fun register(type: TypeLiteral<*>) = register(Key.get(type))
  override fun register(type: TypeToken<*>) = register(BindingExtensions.getKey(type))

  private fun <E : Event> postLocallySync(event: E, order: Order, clazz: KClass<E>): PostEventResult.InvocationBatch<E> {
    val resultBuilder = PostEventResult.InvocationBatch.Builder()
    for (e in localListeners.get(order, clazz) as Collection<EventListener<E>>) {
      val invocationBuilder = resultBuilder.invocationBuilder()
      try {
        invocationBuilder.startInvoke()
        e.handle(event)
        invocationBuilder.endInvoke()
      } catch (e: Exception) {
        e.printStackTrace()
        invocationBuilder.endExceptionally(e)
      }
      invocationBuilder.finish()
    }
    for (superInterface in clazz.superclasses) {
      if (superInterface.isSubclassOf(Event::class)) {
        resultBuilder.child(postLocallySync(event, order, superInterface as KClass<E>))
      }
    }
    return resultBuilder.build()
  }

  private inline fun <reified T> Sequence<T>.toTypedArray(size: Int): Array<T> {
    val iter = iterator()
    return Array(size) { iter.next() }
  }

  private inline fun <reified E : Event> postSync(event: E, toSend: Map<Order, Set<NodeRef>>): PostEventResult {
    val resultBuilder = PostEventResult.Builder<E>()
    for (order in Order.values()) {
      resultBuilder.ingest(postLocallySync(event, order, E::class))
      val receivedEvents = ConcurrentLinkedDeque<E>()
      val toActuallySend = toSend[order] ?: continue
      println("Sending to ${toActuallySend.joinToString(",")}")
      CompletableFuture.allOf(
        *toActuallySend.asSequence().map { node ->
          anvilNetService.prepareNext<EventPostPacket<E>>().nodeId(node.id).run().thenAccept { packet ->
            if (packet == null) {
              println("Timed out waiting for event result for $order from $node")
              return@thenAccept
            }
            val receivedEvent = packet.eventData.event
            println("Received event order $order from $node: $receivedEvent")
            receivedEvents.add(receivedEvent)
          }
        }.toTypedArray(toActuallySend.size)
      ).join()
      receivedEvents.forEach(event::merge)
    }
    resultBuilder.build()
  }

  private inline fun <reified E : Event> post0(event: E): CompletableFuture<PostEventResult> {
    val toSend: MutableMap<Order, MutableSet<NodeRef>> = mutableMapOf()
    for (nodeRef in pluginMessageNetwork.nodeRefs) {
      for (order in nodeRef.node.eventListeners[E::class] ?: continue) {
        toSend.computeIfAbsent(order) { mutableSetOf() }.add(nodeRef)
      }
    }
    val toPost: (E, Map<Order, Set<NodeRef>>) -> PostEventResult = ::postSync
    return if (event.isAsync) {
      CompletableFuture.supplyAsync { toPost(event, toSend) }
    } else {
      CompletableFuture.completedFuture(toPost(event, toSend))
    }
  }

  override fun post(event: Event): CompletableFuture<PostEventResult> = post0(event)

  override fun <T : Event> register(eventType: Class<in T>, listener: EventListener<T>, order: Order) {
    localListeners.row(order)
      .computeIfAbsent(eventType.kotlin as KClass<out Event>) { ArrayList() }
      .add(listener)
  }
}
