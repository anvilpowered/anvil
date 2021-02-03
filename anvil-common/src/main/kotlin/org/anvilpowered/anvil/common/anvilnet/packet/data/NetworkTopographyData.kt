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
import org.anvilpowered.anvil.api.AnvilImpl
import org.anvilpowered.anvil.common.anvilnet.communicator.node.Connection
import org.anvilpowered.anvil.common.anvilnet.communicator.NetworkHeader
import org.anvilpowered.anvil.common.anvilnet.network.BaseNetwork
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork

/**
 * Used to inform neighboring nodes of the network state beyond their "neighborhood"
 */
class NetworkTopographyData(
    input: ByteArrayDataInput?,
    broadcastNetwork: BroadcastNetwork? = null,
    pluginMessageNetwork: PluginMessageNetwork? = null,
) : DataContainer, NetworkData {

    /**
     * The remote version of the medial network connections.
     */
    lateinit var hub: Array<Connection>
        private set

    /**
     * The remote version of the lateral network connections.
     */
    lateinit var lateral: Array<Connection>
        private set

    constructor(input: ByteArrayDataInput) : this(input, null)

    init {
        if (input == null) {
            requireNotNull(broadcastNetwork)
            requireNotNull(pluginMessageNetwork)
            this.hub = broadcastNetwork.connections.toTypedArray()
            this.lateral = pluginMessageNetwork.connections.toTypedArray()
        } else {
            read(input)
        }
    }

    override fun write(output: ByteArrayDataOutput) {
        val logger = AnvilImpl.getLogger()
        logger.info("Write Hub:")
        for (con in hub) {
            logger.info(con.toString())
        }
        logger.info("Write Lateral:")
        for (con in lateral) {
            logger.info(con.toString())
        }
        output.writeShortContainers(hub)
        output.writeShortContainers(lateral)
    }

    override fun read(input: ByteArrayDataInput) {
        val logger = AnvilImpl.getLogger()
        hub = input.readShortContainers(Connection::class.java)
        lateral = input.readShortContainers(Connection::class.java)
        logger.info("Read Hub:")
        for (con in hub) {
            logger.info(con.toString())
        }
        logger.info("Read Lateral:")
        for (con in lateral) {
            logger.info(con.toString())
        }
    }

    private fun update(remote: Array<Connection>, local: BaseNetwork) {
        for (con in remote) {
            val nodeA = con.nodes.first
            val nodeB = con.nodes.second
            local.ensureNode(nodeA.id, nodeA.name)
            local.ensureNode(nodeB.id, nodeB.name)
            local.ensureConnection(nodeA, nodeB)
        }
    }

    private fun remove(remote: Array<Connection>, local: BaseNetwork) {
        for (con in remote) {
            local.removeConnection(con)
        }
    }

    override fun updateNetwork(
        header: NetworkHeader,
        broadcastNetwork: BroadcastNetwork,
        pluginMessageNetwork: PluginMessageNetwork,
    ) {
        val logger = AnvilImpl.getLogger()
        logger.info("In update method")
        logger.info("PM: ${pluginMessageNetwork.connections}")
        update(hub, broadcastNetwork)
        update(lateral, pluginMessageNetwork)
        logger.info("After update method")
        logger.info("PM: ${pluginMessageNetwork.connections}")
    }
}
