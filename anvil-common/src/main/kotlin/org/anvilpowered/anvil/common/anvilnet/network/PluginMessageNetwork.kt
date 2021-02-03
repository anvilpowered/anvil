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
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.common.anvilnet.ConnectionType
import org.anvilpowered.anvil.common.anvilnet.communicator.node.Node

@Singleton
@Suppress("UnstableApiUsage")
class PluginMessageNetwork @Inject constructor(
    registry: Registry,
) : BaseNetwork(registry, ConnectionType.LATERAL) {

    private val hierarchy: MutableGraph<Node> = GraphBuilder.directed()
        .nodeOrder(ElementOrder.natural<Node>())
        .build()

    fun getChildren(node: Node): Set<Node> = hierarchy.successors(node)
    fun getParent(node: Node): Node? = hierarchy.predecessors(node).firstOrNull()
}
