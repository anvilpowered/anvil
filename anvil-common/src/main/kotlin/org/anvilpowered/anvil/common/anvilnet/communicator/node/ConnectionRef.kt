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
import org.anvilpowered.anvil.common.anvilnet.ConnectionType
import org.anvilpowered.anvil.common.anvilnet.communicator.format
import org.anvilpowered.anvil.common.anvilnet.packet.data.DataContainer
import org.anvilpowered.anvil.common.anvilnet.packet.data.writeContainer

/**
 * A direct connection between two adjacent nodes
 */
class ConnectionRef : DataContainer, Comparable<ConnectionRef> {

  /**
   * @return The set of the two nodes on either end of this connection
   */
  lateinit var nodes: Pair<NodeRef, NodeRef>

  lateinit var connectionType: ConnectionType

  private var hashcode: Int? = null

  val connection: ConnectionImpl by lazy {
    Anvil.getEnvironment().injector.getInstance(NetworkStorage::class.java).getConnection(this)
  }

  constructor(nodeRefA: NodeRef, nodeRefB: NodeRef, type: ConnectionType) {
    setNodes(nodeRefA, nodeRefB)
    this.connectionType = type
  }

  constructor(input: ByteArrayDataInput) {
    read(input)
  }

  override fun write(output: ByteArrayDataOutput) {
    output.writeContainer(nodes.first)
    output.writeContainer(nodes.second)
    output.writeByte(connectionType.ordinal)
  }

  override fun read(input: ByteArrayDataInput) {
    setNodes(NodeRef(input), NodeRef(input))
    connectionType = ConnectionType.values()[input.readUnsignedByte()]
  }

  private fun setNodes(nodeRefA: NodeRef, nodeRefB: NodeRef, cmp: Int = nodeRefA.compareTo(nodeRefB)) {
    require(cmp != 0) { "nodeA cannot be the same as nodeB" }
    nodes = if (cmp < 0) {
      Pair(nodeRefA, nodeRefB)
    } else {
      Pair(nodeRefB, nodeRefA)
    }
    hashcode = nodeRefA.hashCode() * nodeRefB.hashCode()
  }

  fun containsNodeId(nodeId: Int): Boolean = nodes.first.id == nodeId || nodes.second.id == nodeId

  override fun equals(other: Any?): Boolean {
    return this === other || other is ConnectionRef && compareTo(other) == 0
  }

  override fun compareTo(other: ConnectionRef): Int {
    val cmpA = nodes.first.compareTo(other.nodes.first)
    return if (cmpA == 0) {
      val cmpB = nodes.second.compareTo(other.nodes.second)
      return if (cmpB == 0) {
        connectionType.compareTo(other.connectionType)
      } else cmpB
    } else cmpA
  }

  override fun hashCode(): Int = hashcode!!

  private val stringRepresentation: String by lazy {
    MoreObjects.toStringHelper(this)
      .add("nodeA", nodes.first)
      .add("nodeB", nodes.second)
      .add("type", connectionType)
      .toString()
  }

  private val stringRepresentationCompact: String by lazy {
    "ConnectionRef{${nodes.first.id.format()}<->${nodes.second.id.format()}}"
  }

  override fun toString(): String = stringRepresentation
  fun toStringCompact(): String = stringRepresentationCompact
}
