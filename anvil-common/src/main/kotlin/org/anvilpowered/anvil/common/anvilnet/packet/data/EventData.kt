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

import com.google.common.base.MoreObjects
import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.api.event.Order
import kotlin.reflect.KClass

class EventData<E : Event> : DataContainer {

  lateinit var eventType: KClass<E>
    private set

  lateinit var order: Order
    private set

  lateinit var event: E
    private set

  constructor(eventType: KClass<E>, order: Order, event: E) {
    this.eventType = eventType
    this.order = order
    this.event = event
  }

  constructor(input: ByteArrayDataInput) {
    read(input)
  }

  override fun write(output: ByteArrayDataOutput) {
    output.writeKClass(eventType)
    output.writeOrder(order)
    output.writeAsJson(event)
  }

  override fun read(input: ByteArrayDataInput) {
    eventType = input.readKClass()
    order = input.readOrder()
    event = input.readAsJson()
  }

  private val stringRepresentation by lazy {
    MoreObjects.toStringHelper(this)
      .add("eventType", eventType)
      .add("order", order)
      .add("event", event)
      .toString()
  }

  override fun toString(): String = stringRepresentation
}
