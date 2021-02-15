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

import com.google.common.io.ByteArrayDataInput
import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.common.anvilnet.communicator.NetworkHeader
import org.anvilpowered.anvil.common.anvilnet.packet.data.BaseData
import kotlin.reflect.KClass

class EventListenerPacket : AnvilNetPacket {

  constructor(eventTypes: List<KClass<out Event>>) : super(BaseData())

  constructor(header: NetworkHeader, input: ByteArrayDataInput) : super(header, BaseData(input))
}
