/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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
package org.anvilpowered.anvil.common.anvilnet.network

import com.google.common.graph.ElementOrder
import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import com.google.inject.Singleton
import org.anvilpowered.anvil.common.anvilnet.ConnectionType
import org.anvilpowered.anvil.common.anvilnet.communicator.node.ConnectionRef
import org.anvilpowered.anvil.common.anvilnet.communicator.node.NodeRef

@Singleton
@Suppress("UnstableApiUsage")
class PluginMessageNetwork : BaseNetwork(ConnectionType.VERTICAL) {

  private val hierarchy: MutableGraph<NodeRef> = GraphBuilder.directed()
    .nodeOrder(ElementOrder.natural<NodeRef>())
    .build()

  fun getChildren(nodeRef: NodeRef): Set<NodeRef> = hierarchy.successors(nodeRef)
  fun getParent(nodeRef: NodeRef): NodeRef? = hierarchy.predecessors(nodeRef).firstOrNull()
  fun getConnectionRefTo(nodeId: Int): ConnectionRef {
    return getAdjacentConnections().asSequence()
      .filter { it.containsNodeId(nodeId) }.firstOrNull()
      ?: throw AssertionError("Connection to $nodeId is missing")
  }
}
