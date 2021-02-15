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

import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.api.event.EventListener
import org.anvilpowered.anvil.api.event.EventListenerResult
import org.anvilpowered.anvil.common.anvilnet.packet.data.EventListenerMetaImpl
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

class MethodEventListener<E : Event>(
  /**
   * The type that contains the method
   */
  private val enclosingType: KClass<*>,
  /**
   * The method
   */
  private val method: KCallable<EventListenerResult>,
  /**
   * Produces the correct parameters for an invokation of [method] for a given event [E]
   */
  private val parser: (KCallable<EventListenerResult>, E) -> Array<Any>,
  /**
   * Get your instances here!
   */
  private val instanceSupplier: () -> Any,
  private val meta: EventListenerMetaImpl<E>,
) : EventListener<E> {

  override fun getMeta(): EventListenerMetaImpl<E> = meta

  @Throws(Exception::class)
  override fun handle(event: E): EventListenerResult {
    return method.call(instanceSupplier(), *parser(method, event))
  }
}
