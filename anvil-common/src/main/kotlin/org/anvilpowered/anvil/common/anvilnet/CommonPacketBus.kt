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

package org.anvilpowered.anvil.common.anvilnet

import com.google.inject.Inject
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import org.anvilpowered.anvil.common.anvilnet.packet.AnvilNetPacket
import org.anvilpowered.anvil.common.anvilnet.packet.DeadNodePacket
import org.anvilpowered.anvil.common.anvilnet.packet.InitialPingPacket
import org.slf4j.Logger
import java.util.UUID
import java.util.concurrent.CompletableFuture

class CommonPacketBus {

    @Inject
    private lateinit var anvilNetService: CommonAnvilNetService

    @Inject
    private lateinit var broadcastNetwork: BroadcastNetwork

    @Inject
    private lateinit var pluginMessageNetwork: PluginMessageNetwork

    @Inject
    private lateinit var logger: Logger

    fun send(packet: AnvilNetPacket, connectionType: ConnectionType? = null) {
        try {
            val result = anvilNetService.prepare(packet)
                .connectionType(connectionType)
                .send().join()
            if (!result) {
                logger.error("[Send] failed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendAsync(packet: AnvilNetPacket, connectionType: ConnectionType? = null, delay: Long = 4000) {
        CompletableFuture.runAsync {
            try {
                Thread.sleep(delay)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            send(packet, connectionType)
        }
    }

    fun onPlayerJoin(userUUID: UUID) {
        sendAsync(InitialPingPacket(userUUID), ConnectionType.LATERAL)
    }

    fun onServerStop() {
        println("Sending server stop stuff")
        broadcastNetwork.current?.id?.also { send(DeadNodePacket(it), ConnectionType.HUB) }
        pluginMessageNetwork.current?.id?.also { send(DeadNodePacket(it), ConnectionType.LATERAL) }
    }
}
