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
package org.anvilpowered.anvil.common.anvilnet.network

import com.google.common.graph.ElementOrder
import com.google.common.graph.MutableNetwork
import com.google.common.graph.NetworkBuilder
import org.anvilpowered.anvil.api.AnvilImpl
import org.anvilpowered.anvil.common.anvilnet.ConnectionType
import org.anvilpowered.anvil.common.anvilnet.communicator.format
import org.anvilpowered.anvil.common.anvilnet.communicator.node.ConnectionRef
import org.anvilpowered.anvil.common.anvilnet.communicator.node.NodeRef

@Suppress("UnstableApiUsage")
abstract class BaseNetwork(
  private val connectionType: ConnectionType
) {
  val network: MutableNetwork<NodeRef, ConnectionRef> = NetworkBuilder.undirected()
    .nodeOrder(ElementOrder.natural<NodeRef>())
    .edgeOrder(ElementOrder.natural<ConnectionRef>())
    .build()

  val nodeRefs: Set<NodeRef> = network.nodes()
  val connectionRefs: Set<ConnectionRef> = network.edges()
  lateinit var current: NodeRef

  fun initCurrentNode(nodeId: Int, nodeName: String) {
    require(!this::current.isInitialized) { "current node already initialized" }
    current = ensureNode(nodeId, nodeName)
  }

  fun getAdjacentConnections(nodeRef: NodeRef = current): Set<ConnectionRef> = network.incidentEdges(nodeRef)
  fun getAdjacentNodes(nodeRef: NodeRef = current): Set<NodeRef> = network.adjacentNodes(nodeRef)

  fun ensureNode(nodeId: Int, nodeName: String? = null): NodeRef {
    AnvilImpl.getLogger().info("Ensuring node $nodeName with nodeId ${nodeId.format()}")
    return nodeRefs.find { it.id == nodeId }
      ?: NodeRef(nodeId, nodeName).also { network.addNode(it) }
  }

  fun ensureConnection(nodeRefA: NodeRef, nodeRefB: NodeRef = current): ConnectionRef {
    AnvilImpl.getLogger().info("Ensuring connection $connectionType between $nodeRefA and $nodeRefB")
    return network.edgesConnecting(nodeRefA, nodeRefB).firstOrNull()
      ?: ConnectionRef(nodeRefA, nodeRefB, connectionType).also { network.addEdge(nodeRefA, nodeRefB, it) }
  }

  fun removeNode(nodeRef: NodeRef) {
    AnvilImpl.getLogger().info("Removing nodeId ${nodeRef.id} from $connectionType")
    network.removeNode(nodeRef)
  }

  fun removeConnection(connectionRef: ConnectionRef) {
    AnvilImpl.getLogger().info(
      "Removing connection $connectionType between ${connectionRef.nodes.first} and ${connectionRef.nodes.second}"
    )
    network.removeEdge(connectionRef)
    if (network.adjacentEdges(connectionRef.nodes.first).isEmpty()) {
      network.removeNode(connectionRef.nodes.first)
    }
    if (network.adjacentEdges(connectionRef.nodes.second).isEmpty()) {
      network.removeNode(connectionRef.nodes.second)
    }
  }
}
