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
import org.anvilpowered.anvil.api.event.EventListenerResult
import org.anvilpowered.anvil.api.event.EventManager
import org.anvilpowered.anvil.api.event.EventPostResult
import org.anvilpowered.anvil.api.event.Listener
import org.anvilpowered.anvil.api.event.Order
import org.anvilpowered.anvil.api.misc.BindingExtensions
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.common.anvilnet.CommonAnvilNetService
import org.anvilpowered.anvil.common.anvilnet.communicator.node.NodeRef
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import org.anvilpowered.anvil.common.anvilnet.packet.EventPostPacket
import org.anvilpowered.anvil.common.anvilnet.packet.EventResultPacket
import org.anvilpowered.anvil.common.anvilnet.packet.data.EventListenerMetaImpl
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
    registry.whenLoaded(::registryLoaded).register()
  }

  private var alreadyLoaded = false
  private fun registryLoaded() {
    if (alreadyLoaded) {
      return
    }
    println("Registering listener in eventmanager")
    anvilNetService.prepareRegister<EventPostPacket<*>> {
      logger.info("RECEIVED EVENT: ${it.eventData.event}")
      receiveEventPost(it)
    }.register()
    alreadyLoaded = true
  }

  private fun parseListener(clazz: KClass<out Any>, supplier: () -> Any) {
    for (method in clazz.members) {
      val annotation = method.findAnnotation<Listener>() ?: continue
      val parameters = method.parameters
      check(parameters.size == 2) {
        "Method annotated with @Listener must have exactly one parameter!"
      }
      check(method.returnType.jvmErasure == EventListenerResult::class) {
        "Method annotated with @Listener must have EventListenerResult return type"
      }
      val eventType = parameters[1].type.jvmErasure
      check(eventType.isSubclassOf(Event::class)) {
        "First parameter of listener must be an event type!"
      }
      method.isAccessible = true
      localListeners.row(annotation.order)
        .computeIfAbsent(eventType as KClass<out Event>) { ArrayList() }
        .add(
          MethodEventListener(
            clazz,
            method as KCallable<EventListenerResult>,
            { _, e: Event -> arrayOf(e) },
            supplier,
            EventListenerMetaImpl(eventType, annotation)
          )
        )
    }
  }

  private fun <E : Event> receiveEventPost(eventPostPacket: EventPostPacket<E>) {
    val eventData = eventPostPacket.eventData
    val tree = postLocallySync(eventData.eventType, eventData.event, eventData.order)
    logger.info("EventData: $eventData")
    logger.info("EventTree: $tree")
    anvilNetService.prepareSend(EventResultPacket(tree, eventData)).target(eventPostPacket.header!!.path.sourceId).send()
  }

  private fun <E : Event> postLocallySync(eventType: KClass<E>, event: E, order: Order): EventPostResultImpl.Tree<E> {
    val tree = EventPostResultImpl.Tree(eventType, pluginMessageNetwork.current.id)
    for (listener in (localListeners.get(order, eventType) as? Collection<EventListener<E>>) ?: return tree) {
      val invocation = EventPostResultImpl.Invocation<E>()
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
        tree.addChild(postLocallySync(superInterface as KClass<E>, event, order))
      }
    }
    return tree
  }

  private fun <E : Event> post(
    eventType: KClass<E>,
    event: E,
    maxWait: Long,
    toWait: Map<Order, List<NodeRef>>,
  ): EventPostResultImpl<E> {
    logger.info("Foo")
    val result = EventPostResultImpl(eventType)
    val allNextFutures: MutableList<CompletableFuture<EventResultPacket<E>?>> = mutableListOf()
    val allCombinedFutures: MutableList<CompletableFuture<Void>> = ArrayList(Order.values().size)
    for (order in Order.values()) {
      val batch = EventPostResultImpl.Batch<E>(order)
      batch.addTree(postLocallySync(eventType, event, order))
      val receivedEvents = ConcurrentLinkedDeque<E>()
      val toActuallyWait = toWait[order] ?: continue
      anvilNetService.prepareSend(EventPostPacket(eventType, event, order)).send()
      logger.info("Waiting for ${toActuallyWait.joinToString(",")}")
      val combinedFuture = CompletableFuture.allOf(
        *Array(toActuallyWait.size) { index ->
          val node = toActuallyWait[index]
          val nextPacketFuture = anvilNetService.prepareNext<EventResultPacket<E>> { packet ->
            val type = packet.eventResultData.tree.eventTypeKt
            val t = type == eventType
            println("Comparing $type with $eventType : $t")
            t
          }.nodeId(node.id).run()
          allNextFutures.add(nextPacketFuture)
          nextPacketFuture.thenAccept { packet ->
            if (packet == null) {
              logger.info("Timed out waiting for event result for $order from $node")
              batch.addTree(EventPostResultImpl.Tree(eventType))
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

  override fun <E : Event> post(eventType: Class<E>, event: E, maxWait: Long): CompletableFuture<EventPostResult<E>> {
    check(eventType.isInstance(event)) { "Event is not instance of $eventType" }
    val toWait: MutableMap<Order, MutableList<NodeRef>> = mutableMapOf()
    val eventTypeKt = eventType.kotlin
    for (nodeRef in pluginMessageNetwork.nodeRefs) {
      for (order in nodeRef.node.eventListeners[eventTypeKt] ?: continue) {
        toWait.computeIfAbsent(order) { mutableListOf() }.add(nodeRef)
      }
    }
    logger.info("ToWait: $toWait")
    return if (event.isAsync) {
      CompletableFuture.completedFuture(post(eventTypeKt, event, maxWait, toWait))
      //CompletableFuture.supplyAsync { post(eventTypeKt, event, maxWait, toWait) }
    } else {
      CompletableFuture.completedFuture(post(eventTypeKt, event, maxWait, toWait))
    }
  }

  override fun <E : Event> post(eventType: Class<E>, event: E): CompletableFuture<EventPostResult<E>> = post(eventType, event, 0)
  override fun register(listener: Any) = parseListener(listener::class) { listener }
  override fun register(type: Class<*>) = parseListener(type.kotlin) { injector.getInstance(type) }
  override fun register(key: Key<*>) = parseListener(key.typeLiteral.rawType.kotlin) { injector.getInstance(key) }
  override fun register(type: TypeLiteral<*>) = register(Key.get(type))
  override fun register(type: TypeToken<*>) = register(BindingExtensions.getKey(type))
  override fun <E : Event> register(listener: EventListener<E>) {
    localListeners.row(listener.meta.order)
      .computeIfAbsent(listener.meta.eventType.kotlin) { ArrayList() }
      .add(listener)
  }

  fun getRegisteredEvents(): Array<EventListenerMetaImpl<out Event>> {
    val result = ArrayList<EventListenerMetaImpl<out Event>>(localListeners.size() * 2)
    for (row in localListeners.rowMap().values) {
      for (col in row.values) {
        for (listener in col) {
          result.add(EventListenerMetaImpl.from(listener.meta))
        }
      }
    }
    return result.toTypedArray()
  }
}
