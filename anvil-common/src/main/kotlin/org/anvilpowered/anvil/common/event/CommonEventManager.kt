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
import org.anvilpowered.anvil.api.event.EventResult
import org.anvilpowered.anvil.api.event.Listener
import org.anvilpowered.anvil.api.event.Order
import org.anvilpowered.anvil.api.misc.BindingExtensions
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.common.anvilnet.CommonAnvilNetService
import org.anvilpowered.anvil.common.anvilnet.communicator.node.NodeRef
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import org.anvilpowered.anvil.common.anvilnet.packet.EventPostPacket
import org.anvilpowered.anvil.common.anvilnet.packet.EventResultPacket
import org.slf4j.Logger
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

@Singleton
class CommonEventManager @Inject constructor(private val registry: Registry) : EventManager {

  @Inject
  private lateinit var anvilNetService: CommonAnvilNetService

  @Inject
  private lateinit var injector: Injector

  @Inject
  private lateinit var logger: Logger

  @Inject
  private lateinit var pluginMessageNetwork: PluginMessageNetwork

  private val localListeners: Table<Order, KClass<out Event>, MutableCollection<EventListener<*>>>

  init {
    localListeners = HashBasedTable.create()
    registry.whenLoaded(::registryLoaded)
  }

  private var alreadyLoaded = false
  private fun registryLoaded() {
    if (alreadyLoaded) {
      return
    }
    anvilNetService.prepareRegister<EventPostPacket<*>> { receiveEventPost(it) }
    alreadyLoaded = true
  }

  private fun parseListener(clazz: KClass<out Any>, supplier: () -> Any) {
    for (method in clazz.members) {
      val annotation = method.findAnnotation<Listener>() ?: continue
      val parameters = method.parameters
      check(parameters.size == 1) { "Method annotated with @Listener must have exactly one parameter!" }
      check(method.returnType.jvmErasure == Void::class) { "Method annotated with @Listener must have void return type" }
      val eventType = parameters[0].type.jvmErasure
      check(eventType.isSubclassOf(Event::class)) { "First parameter of listener must be an event type!" }
      method.isAccessible = true
      localListeners.row(annotation.order)
        .computeIfAbsent(eventType as KClass<out Event>) { ArrayList() }
        .add(MethodEventListener(clazz, method as KCallable<Unit>, { _, e: Event -> arrayOf(e) }, supplier))
    }
  }

  private fun <E : Event> receiveEventPost(eventPostPacket: EventPostPacket<E>) {
    val eventData = eventPostPacket.eventData
    val tree = postLocallySync(eventData.event, eventData.order, eventData.eventType)
    anvilNetService.prepareSend(EventResultPacket(tree, eventData)).target(eventPostPacket.header!!.path.sourceId).send()
  }

  private fun <E : Event> postLocallySync(event: E, order: Order, eventType: KClass<E>): EventResultImpl.Tree<E> {
    val tree = EventResultImpl.Tree(eventType)
    for (listener in localListeners.get(order, eventType) as Collection<EventListener<E>>) {
      val invocation = EventResultImpl.Invocation<E>()
      try {
        invocation.startInvoke()
        listener.handle(event)
        invocation.endInvoke()
      } catch (e: Exception) {
        logger.error("An error occurred invoking event listener $listener", e)
        invocation.endExceptionally(e)
      }
      tree.addInvocation(invocation)
    }
    for (superInterface in eventType.superclasses) {
      if (superInterface.isSubclassOf(Event::class)) {
        tree.addChild(postLocallySync(event, order, superInterface as KClass<E>))
      }
    }
    return tree
  }

  private fun <E : Event> post(
    event: E,
    eventType: KClass<E>,
    maxWait: Long,
    toWait: Map<Order, List<NodeRef>>,
  ): EventResultImpl<E> {
    val result = EventResultImpl(eventType)
    val allNextFutures: MutableList<CompletableFuture<EventResultPacket<E>?>> = mutableListOf()
    val allCombinedFutures: MutableList<CompletableFuture<Void>> = ArrayList(Order.values().size)
    for (order in Order.values()) {
      val batch = EventResultImpl.Batch<E>(order)
      batch.addTree(postLocallySync(event, order, eventType))
      val receivedEvents = ConcurrentLinkedDeque<E>()
      val toActuallyWait = toWait[order] ?: continue
      anvilNetService.prepareSend(EventPostPacket(eventType, event, order)).send()
      logger.info("Waiting for ${toActuallyWait.joinToString(",")}")
      val combinedFuture = CompletableFuture.allOf(
        *Array(toActuallyWait.size) { index ->
          val node = toActuallyWait[index]
          val nextPacketFuture = anvilNetService.prepareNext<EventResultPacket<E>> { packet ->
            packet.eventResultData.tree.parentBatch.parentResult.eventType == eventType
          }.nodeId(node.id).run()
          allNextFutures.add(nextPacketFuture)
          nextPacketFuture.thenAccept { packet ->
            if (packet == null) {
              logger.info("Timed out waiting for event result for $order from $node")
              return@thenAccept
            }
            val receivedEvent = packet.eventData.event
            logger.info("Received event order $order from $node: $receivedEvent")
            receivedEvents.add(receivedEvent)
            batch.addTree(packet.eventResultData.tree)
          }
        }
      )
      allCombinedFutures.add(combinedFuture)
      if (event.isExternallyBlockable) {
        combinedFuture.join()
      }
      receivedEvents.forEach(event::merge)
      result.addBatch(batch)
    }
    if (!event.isExternallyBlockable && maxWait > 0) {
      val bigFuture = CompletableFuture.allOf(*allCombinedFutures.toTypedArray())
      if (!bigFuture.isDone) {
        val waitFuture = CompletableFuture.supplyAsync {
          try {
            Thread.sleep(maxWait)
          } catch (ignored: InterruptedException) {
            logger.info("Finished before timeout")
          }
          logger.info("Timeout $maxWait reached, cancelling listeners")
          allNextFutures.forEach { it.complete(null) }
        }
        bigFuture.thenAccept {
          logger.info("Big future finished, cancelling timeout")
          waitFuture.cancel(true)
        }
        waitFuture.join()
      }
    }
    return result
  }

  override fun <E : Event> post(event: E, eventType: Class<E>, maxWait: Long): CompletableFuture<EventResult<E>> {
    check(eventType.isInstance(event)) { "Event is not instance of $eventType" }
    val toWait: MutableMap<Order, MutableList<NodeRef>> = mutableMapOf()
    val eventTypeKt = eventType.kotlin
    for (nodeRef in pluginMessageNetwork.nodeRefs) {
      for (order in nodeRef.node.eventListeners[eventTypeKt] ?: continue) {
        toWait.computeIfAbsent(order) { mutableListOf() }.add(nodeRef)
      }
    }
    return if (event.isAsync) {
      CompletableFuture.supplyAsync { post(event, eventTypeKt, maxWait, toWait) }
    } else {
      CompletableFuture.completedFuture(post(event, eventTypeKt, maxWait, toWait))
    }
  }

  override fun <E : Event> post(event: E, eventType: Class<E>): CompletableFuture<EventResult<E>> = post(event, eventType, 0)
  override fun register(listener: Any) = parseListener(listener::class) { listener }
  override fun register(type: Class<*>) = parseListener(type.kotlin) { injector.getInstance(type) }
  override fun register(key: Key<*>) = parseListener(key.typeLiteral.rawType.kotlin) { injector.getInstance(key) }
  override fun register(type: TypeLiteral<*>) = register(Key.get(type))
  override fun register(type: TypeToken<*>) = register(BindingExtensions.getKey(type))
  override fun <T : Event> register(eventType: Class<in T>, listener: EventListener<T>, order: Order) {
    localListeners.row(order)
      .computeIfAbsent(eventType.kotlin as KClass<out Event>) { ArrayList() }
      .add(listener)
  }
}
