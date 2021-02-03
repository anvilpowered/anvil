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
import com.google.common.graph.MutableNetwork
import com.google.common.graph.NetworkBuilder
import org.anvilpowered.anvil.api.AnvilImpl
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.common.anvilnet.ConnectionType
import org.anvilpowered.anvil.common.anvilnet.communicator.node.Connection
import org.anvilpowered.anvil.common.anvilnet.communicator.node.Node
import org.anvilpowered.anvil.common.anvilnet.communicator.format

@Suppress("UnstableApiUsage")
abstract class BaseNetwork(
    registry: Registry,
    private val connectionType: ConnectionType
) {
    val network: MutableNetwork<Node, Connection> = NetworkBuilder.undirected()
        .nodeOrder(ElementOrder.natural<Node>())
        .edgeOrder(ElementOrder.natural<Connection>())
        .build()

    val nodes: Set<Node> = network.nodes()
    val connections: Set<Connection> = network.edges()
    var current: Node? = null

    fun initCurrentNode(nodeId: Int, nodeName: String) {
        require(current == null) { "current node already initialized" }
        current = ensureNode(nodeId, nodeName)
    }

    fun getConnections(node: Node): Set<Connection> = network.incidentEdges(node)

    fun ensureNode(nodeId: Int, nodeName: String): Node {
        AnvilImpl.getLogger().info("Ensuring node $nodeName with nodeId ${nodeId.format()}")
        return nodes.find { it.name == nodeName }
            ?: Node(nodeId, nodeName).also { network.addNode(it) }
    }

    fun ensureConnection(nodeA: Node, nodeB: Node = current!!): Connection {
        AnvilImpl.getLogger().info("Ensuring connection $connectionType between $nodeA and $nodeB")
        return network.edgesConnecting(nodeA, nodeB).firstOrNull()
            ?: Connection(nodeA, nodeB, connectionType).also { network.addEdge(nodeA, nodeB, it) }
    }

    fun removeNode(node: Node) {
        AnvilImpl.getLogger().info("Removing nodeId ${node.id} from $connectionType")
        network.removeNode(node)
    }

    fun removeConnection(connection: Connection) {
        AnvilImpl.getLogger().info(
            "Removing connection $connectionType between ${connection.nodes.first} and ${connection.nodes.second}"
        )
        network.removeEdge(connection)
        if (network.adjacentEdges(connection.nodes.first).isEmpty()) {
            network.removeNode(connection.nodes.first)
        }
        if (network.adjacentEdges(connection.nodes.second).isEmpty()) {
            network.removeNode(connection.nodes.second)
        }
    }
}
