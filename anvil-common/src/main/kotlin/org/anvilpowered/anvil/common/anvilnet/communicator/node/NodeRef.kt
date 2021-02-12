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
package org.anvilpowered.anvil.common.anvilnet.communicator.node

import com.google.common.base.MoreObjects
import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.common.anvilnet.communicator.format
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import org.anvilpowered.anvil.common.anvilnet.packet.data.DataContainer

/**
 * Low-level node representing a server. One per communication type per server.
 */
class NodeRef : DataContainer, Comparable<NodeRef> {

  var id: Int = 0
    private set

  lateinit var name: String
    private set

  val node: Node by lazy { Anvil.getEnvironment().injector.getInstance(NetworkStorage::class.java).getNode(this) }
  private val pluginMessageNetwork by lazy { Anvil.getEnvironment().injector.getInstance(PluginMessageNetwork::class.java) }
  private val broadcastNetwork by lazy { Anvil.getEnvironment().injector.getInstance(BroadcastNetwork::class.java) }

  constructor(id: Int, name: String? = null) {
    this.id = id
    if (name != null) {
      this.name = name
    }
  }

  constructor(input: ByteArrayDataInput) {
    read(input)
  }

  override fun write(output: ByteArrayDataOutput) {
    output.writeInt(id)
    output.writeUTF(name)
  }

  override fun read(input: ByteArrayDataInput) {
    id = input.readInt()
    name = input.readUTF()
  }

  fun getLateralConnections(): Set<ConnectionRef> = pluginMessageNetwork.getAdjacentConnections(this)
  fun getMedialConnections(): Set<ConnectionRef> = broadcastNetwork.getAdjacentConnections(this)
  fun getChildren(): Set<NodeRef> = pluginMessageNetwork.getChildren(this)
  fun getParent(): NodeRef? = pluginMessageNetwork.getParent(this)

  override fun equals(other: Any?): Boolean {
    return if (this === other) {
      true
    } else other is NodeRef && id == other.id
  }

  override fun compareTo(other: NodeRef): Int = id.compareTo(other.id)
  override fun hashCode(): Int = id

  private val stringRepresentation: String by lazy {
    MoreObjects.toStringHelper(this)
      .add("id", this.id.format())
      .add("name", this.name)
      .toString()
  }

  override fun toString(): String = stringRepresentation
}
