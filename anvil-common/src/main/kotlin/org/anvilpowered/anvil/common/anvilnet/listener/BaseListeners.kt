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
package org.anvilpowered.anvil.common.anvilnet.listener

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import org.anvilpowered.anvil.common.anvilnet.CommonAnvilNetService
import org.anvilpowered.anvil.common.anvilnet.communicator.CommonPacketTranslator
import org.anvilpowered.anvil.common.anvilnet.communicator.format
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import org.anvilpowered.anvil.common.anvilnet.packet.AnvilNetPacket
import org.anvilpowered.anvil.common.anvilnet.packet.DeadNodePacket
import org.anvilpowered.anvil.common.anvilnet.packet.InitialPingPacket
import org.anvilpowered.anvil.common.anvilnet.packet.InitialResponsePacket
import org.slf4j.Logger
import java.util.UUID

/**
 * The listeners required to setup and maintain the AnvilNet
 */
class BaseListeners @Inject constructor(
    /**
     * Uses assisted injection to avoid a circular dependency.
     */
    @Assisted private val anvilNetService: CommonAnvilNetService
) {
    interface Factory {
        fun construct(anvilNetService: CommonAnvilNetService): BaseListeners
    }

    @Inject
    private lateinit var logger: Logger

    @Inject
    private lateinit var broadcastNetwork: BroadcastNetwork

    @Inject
    private lateinit var pluginMessageNetwork: PluginMessageNetwork

    @Inject
    private lateinit var packetTranslator: CommonPacketTranslator

    fun registerAll() {
        packetTranslator.registerPacketTypeBegin()
        // NetworkPreJoinPacket must be type 0; This is assumed by the communicator.
        // It will check for nodeId conflicts if the packet type is 0.
        packetTranslator.registerPacketType(InitialPingPacket::class.java)
        packetTranslator.registerPacketType(InitialResponsePacket::class.java)
        packetTranslator.registerPacketType(DeadNodePacket::class.java)
        logger.info("[AnvilNet] Successfully registered {} packet types", packetTranslator.packetTypesSize)
        anvilNetService.register(InitialPingPacket::class.java) { onInitialPing(it) }
        anvilNetService.nextPacketFuture(InitialResponsePacket::class.java)
            .thenAcceptAsync { onInitialResponse(it) }
    }

    private fun received(packet: AnvilNetPacket) {
        logger.info("[Receive] $packet")
    }

    private fun timeout() {
        logger.debug(
            "Did not receive a response from the network within {}ms, assuming alone",
            CommonAnvilNetService.TIMEOUT_MILLIS
        )
    }

    private fun onInitialPing(packet: InitialPingPacket) {
        received(packet)
        // check whether requested name conflicts with this node
        requireNotNull(packet.header) { "packet header" }
        val userUUID: UUID = packet.playerData.userUUID ?: return
        val source = packet.header.path.source
        val name = packet.baseData.nodeName
        println("Sending back to source: ${source.format()}")
        val idOk = pluginMessageNetwork.nodes.find { it.id == source }
            ?: broadcastNetwork.nodes.find { it.id == source } == null
        val nameOk = pluginMessageNetwork.nodes.find { it.name == name }
            ?: broadcastNetwork.nodes.find { it.name == name } == null
        println("idOk: $idOk, nameOk: $nameOk")
        anvilNetService.prepare(
            InitialResponsePacket(userUUID, broadcastNetwork, pluginMessageNetwork)
        ).target(source).send()
        /*if (idOk && nameOk) {
            return
        }
        val reason = if (!idOk && nameOk) {
            "Invalid id"
        } else if (idOk && !nameOk) {
            "Invalid name"
        } else {
            "Invalid id and name"
        }*/
        //anvilNetService.prepare(JoinRequestDeniedPacket(reason)).target(source).send()
    }

    private fun onInitialResponse(packet: InitialResponsePacket?) {
        if (packet == null) {
            timeout()
            return
        }
        println("Denied: ${packet.playerData.userUUID}")
        received(packet)
    }
}
