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

package org.anvilpowered.anvil.common.anvilnet.packet

import com.google.common.base.MoreObjects
import com.google.common.io.ByteArrayDataInput
import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.api.event.Order
import org.anvilpowered.anvil.common.anvilnet.communicator.NetworkHeader
import org.anvilpowered.anvil.common.anvilnet.packet.data.BaseData
import org.anvilpowered.anvil.common.anvilnet.packet.data.EventData
import kotlin.reflect.KClass

/**
 * Represents a single cycle of event execution
 */
class EventPostPacket<E : Event> : AnvilNetPacket {

  constructor(eventType: KClass<E>, event: E, order: Order) : super(
    BaseData(),
    EventData(eventType, order, event),
  )

  constructor(header: NetworkHeader, input: ByteArrayDataInput) : super(
    header,
    BaseData(input),
    EventData<E>(input),
  )

  val baseData: BaseData = getContainer(BaseData::class)!!
  val eventData: EventData<E> = getContainer(EventData::class) as EventData<E>

  private val stringRepresentation: String by lazy {
    MoreObjects.toStringHelper(this)
      .add("baseData", baseData)
      .add("eventData", eventData)
      .toString()
  }

  override fun toString(): String = stringRepresentation
}
