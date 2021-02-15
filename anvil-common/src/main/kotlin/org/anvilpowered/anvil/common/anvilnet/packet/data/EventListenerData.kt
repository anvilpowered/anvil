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
import org.anvilpowered.anvil.api.event.Order
import org.anvilpowered.anvil.common.anvilnet.communicator.NetworkHeader
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import java.util.EnumSet

class EventListenerData : DataContainer, NetworkData {

  lateinit var eventMetas: Array<EventListenerMetaImpl<out Event>>
    private set

  constructor(eventMetas: Array<EventListenerMetaImpl<out Event>>) {
    this.eventMetas = eventMetas
  }

  constructor(input: ByteArrayDataInput) {
    read(input)
  }

  override fun read(input: ByteArrayDataInput) {
    eventMetas = input.readShortContainersAsArray()
  }

  override fun write(output: ByteArrayDataOutput) {
    output.writeShortContainers(eventMetas)
  }

  override fun updateNetwork(
    header: NetworkHeader,
    broadcastNetwork: BroadcastNetwork,
    pluginMessageNetwork: PluginMessageNetwork
  ) {
    val local = pluginMessageNetwork.ensureNode(header.path.sourceId).node.eventListeners
    for (meta in eventMetas) {
      local.computeIfAbsent(meta.eventTypeKt) { EnumSet.noneOf(Order::class.java) }
        .add(meta.order)
    }
  }
}
