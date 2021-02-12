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
import org.anvilpowered.anvil.common.anvilnet.communicator.NetworkHeader
import org.anvilpowered.anvil.common.anvilnet.communicator.format
import org.anvilpowered.anvil.common.anvilnet.communicator.node.NodeRef
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork

/**
 * The node with the enclosed nodeId has died, RIP
 */
class DeadNodeData : DataContainer, NetworkData {

  var nodeId: Int = 0
    private set

  constructor(nodeId: Int) {
    this.nodeId = nodeId
  }

  constructor(input: ByteArrayDataInput) {
    read(input)
  }

  override fun read(input: ByteArrayDataInput) {
    nodeId = input.readInt()
  }

  override fun write(output: ByteArrayDataOutput) {
    output.writeInt(nodeId)
  }

  override fun updateNetwork(
    header: NetworkHeader,
    broadcastNetwork: BroadcastNetwork,
    pluginMessageNetwork: PluginMessageNetwork
  ) {
    val node = NodeRef(nodeId)
    broadcastNetwork.removeNode(node)
    pluginMessageNetwork.removeNode(node)
  }

  private val stringRepresentation: String by lazy {
    MoreObjects.toStringHelper(this)
      .add("nodeId", nodeId.format())
      .toString()
  }

  override fun toString(): String = stringRepresentation
}
