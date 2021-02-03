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
package org.anvilpowered.anvil.sponge.anvilnet.communicator

import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.common.anvilnet.communicator.NetworkHeader
import org.anvilpowered.anvil.common.anvilnet.communicator.PluginMessageCommunicator
import org.spongepowered.api.Platform
import org.spongepowered.api.Sponge
import org.spongepowered.api.network.ChannelBinding.RawDataChannel
import org.spongepowered.api.network.ChannelBuf
import org.spongepowered.api.network.RemoteConnection
import org.spongepowered.api.plugin.PluginContainer
import java.io.ByteArrayInputStream
import java.net.InetSocketAddress
import java.util.Comparator
import java.util.TreeSet
import java.util.UUID
import java.util.function.Consumer

@Singleton
class SpongePluginMessageCommunicator @Inject constructor(
    plugin: PluginContainer
) : PluginMessageCommunicator() {

    private var rawDataChannel: RawDataChannel = Sponge.getChannelRegistrar().getOrCreateRaw(plugin, CHANNEL_NAME)

    init {
        rawDataChannel.addListener(::onMessage)
    }

    private fun onMessage(data: ChannelBuf, connection: RemoteConnection, side: Platform.Type) {
        val inputStream: ByteArrayInputStream = ChannelBufByteArrayInputStream(data)
        val header = accept(inputStream) ?: return
        val source = header.path.source
        if (source != 0) {
            for (player in Sponge.getServer().onlinePlayers) {
                if (player.connection.address == connection.address) {
                    putSink(source, player.uniqueId)
                }
            }
        }
        forward(header, inputStream)
    }

    private fun write(data: ByteArray): Consumer<ChannelBuf> = Consumer { it.writeBytes(data) }

    override fun sendDirect(header: NetworkHeader, data: ByteArray, sinkUUID: UUID?): Boolean {
        val target = header.path.target
        if (sinkUUID != null) {
            Sponge.getServer().getPlayer(sinkUUID).ifPresent { rawDataChannel.sendTo(it, write(data)) }
            if (target != 0) {
                putSink(target, sinkUUID)
            }
            logSent(header, data)
            return true
        }
        if (target != 0) {
            val sinks = getSinks(target)
            while (sinks.hasNext()) {
                val userUUID = sinks.next()
                val player = Sponge.getServer().getPlayer(userUUID)
                if (!player.isPresent) {
                    sinks.remove(nodeId, userUUID)
                    continue
                }
                rawDataChannel.sendTo(player.get(), write(data))
                logSent(header, data)
                return true
            }
        }
        // cant find sink for target, just send to all
        return sendToAll(header, data)
    }

    override fun sendToAll(header: NetworkHeader, data: ByteArray): Boolean {
        val source = header.path.source
        val alreadySent: MutableSet<InetSocketAddress> = TreeSet(Comparator.comparing { it.hostString })
        var result = false
        for (player in Sponge.getServer().onlinePlayers) {
            val address = player.connection.address
            if (!alreadySent.contains(address) && isNotSink(source, player.uniqueId)) {
                rawDataChannel.sendTo(player, write(data))
                logSent(header, data)
                alreadySent.add(address)
                result = true
            }
        }
        return result
    }
}
