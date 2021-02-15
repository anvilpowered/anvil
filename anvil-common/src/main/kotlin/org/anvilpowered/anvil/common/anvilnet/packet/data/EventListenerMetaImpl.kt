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

package org.anvilpowered.anvil.common.anvilnet.packet.data

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.api.event.EventListenerMeta
import org.anvilpowered.anvil.api.event.Listener
import org.anvilpowered.anvil.api.event.Order
import kotlin.reflect.KClass

class EventListenerMetaImpl<E : Event> : EventListenerMeta<E>, DataContainer {

  companion object {
    fun <E : Event>from(meta: EventListenerMeta<E>): EventListenerMetaImpl<E> {
      if (meta is EventListenerMetaImpl) {
        return meta
      }
      return EventListenerMetaImpl(
        meta.eventType.kotlin,
        meta.order,
        meta.isAsync
      )
    }
  }

  lateinit var eventTypeKt: KClass<E>
  private lateinit var eventTypeJava: Class<E>
  private lateinit var order: Order
  private var async: Boolean? = null

  constructor(
    eventType: KClass<E>,
    order: Order,
    async: Boolean,
  ) {
    this.eventTypeKt = eventType
    this.eventTypeJava = eventType.java
    this.order = order
    this.async = async
  }

  constructor(eventType: KClass<E>, annotation: Listener) : this(eventType, annotation.order, annotation.async)

  constructor(input: ByteArrayDataInput) {
    read(input)
  }

  override fun getEventType(): Class<E> = eventTypeJava
  override fun getOrder(): Order = order
  override fun isAsync(): Boolean = async!!

  override fun read(input: ByteArrayDataInput) {
    eventTypeKt = input.readKClass()
    order = input.readOrder()
    async = input.readBoolean()
  }

  override fun write(output: ByteArrayDataOutput) {
    output.writeKClass(eventTypeKt)
    output.writeOrder(order)
    output.writeBoolean(async!!)
  }
}
