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
import org.anvilpowered.anvil.api.misc.BindingExtensions
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Supplier

@Singleton
class CommonEventManager @Inject constructor() : EventManager {
    @Inject
    private lateinit var injector: Injector

    private val localListeners: Table<Order, Class<out Event>, MutableCollection<EventListener<*>>>

    init {
        localListeners = HashBasedTable.create()
    }

    private fun parseListener(clazz: Class<*>, supplier: Supplier<*>) {
        for (method in clazz.methods) {
            val annotation = method.getAnnotation(Listener::class.java) ?: continue
            val parameters = method.parameters
            check(parameters.size == 1) { "Method annotated with @Listener must have exactly one parameter!" }
            val eventType = parameters[0].type
            check(Event::class.java.isAssignableFrom(eventType)) { "First parameter of listener must be an event type!" }
            method.isAccessible = true
            localListeners.row(annotation.order)
                .computeIfAbsent(eventType as Class<out Event>) { ArrayList() }
                .add(MethodEventListener(method, { _, e: Event -> arrayOf(e) }, supplier))
        }
    }

    override fun register(listener: Any) {
        parseListener(listener.javaClass) { listener }
    }

    override fun register(listener: Key<*>) {
        parseListener(listener.typeLiteral.rawType) { injector.getInstance(listener) }
    }

    override fun register(listeners: Iterable<Key<*>>) {
        listeners.forEach(Consumer { listener: Key<*> -> this.register(listener) })
    }

    override fun register(listener: Class<*>) {
        parseListener(listener) { injector.getInstance(listener) }
    }

    override fun register(listener: TypeLiteral<*>?) {
        register(Key.get(listener))
    }

    override fun register(listener: TypeToken<*>?) {
        register(BindingExtensions.getKey(listener))
    }

    private fun <E : Event> postLocallySync(event: E, order: Order, clazz: Class<in E>): Boolean {
        var result = true
        try {
            for (e in localListeners.get(order, clazz) as Collection<EventListener<E>>) {
                e.handle(event)
            }
            for (superInterface in clazz.interfaces) {
                if (Event::class.java.isAssignableFrom(superInterface)
                    && !postLocallySync(event, order, superInterface as Class<in E>)
                ) {
                    result = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return result
    }

    private fun <E : Event> postLocally(event: E, order: Order, clazz: Class<in E>): CompletableFuture<Boolean> {
        return if (event.isAsync) {
            CompletableFuture.supplyAsync { postLocallySync(event, order, clazz) }
        } else {
            CompletableFuture.completedFuture(postLocallySync(event, order, clazz))
        }
    }

    override fun post(event: Event): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            var result = true
            for (order in Order.values()) {
                if (!postLocally(event, order, event.javaClass).join()) {
                    result = false
                }
            }
            result
        }
    }

    override fun <T : Event> register(eventType: Class<in T>, listener: EventListener<T>, order: Order) {
        localListeners.row(order)
            .computeIfAbsent(eventType as Class<out Event>) { ArrayList() }
            .add(listener)
    }
}
