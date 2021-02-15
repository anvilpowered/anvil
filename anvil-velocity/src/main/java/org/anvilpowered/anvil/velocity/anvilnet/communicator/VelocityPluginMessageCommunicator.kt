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
package org.anvilpowered.anvil.velocity.anvilnet.communicator

import com.google.inject.Inject
import com.google.inject.Singleton
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.ChannelIdentifier
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier
import com.velocitypowered.api.proxy.server.ServerInfo
import org.anvilpowered.anvil.common.anvilnet.communicator.NetworkHeader
import org.anvilpowered.anvil.common.anvilnet.communicator.PluginMessageCommunicator
import java.util.UUID

@Singleton
class VelocityPluginMessageCommunicator @Inject constructor(
  private val proxyServer: ProxyServer,
  plugin: PluginContainer,
) : PluginMessageCommunicator() {

  companion object {
    private val CHANNEL: ChannelIdentifier = LegacyChannelIdentifier(CHANNEL_NAME)
  }

  init {
    proxyServer.channelRegistrar.register(CHANNEL)
    proxyServer.eventManager.register(plugin, PluginMessageEvent::class.java, ::onMessage)
  }

  private fun onMessage(event: PluginMessageEvent) {
    if (event.identifier != CHANNEL) {
      return
    }
    val inputStream = event.dataAsInputStream()
    val header = accept(inputStream) ?: return
    val source = header.path.sourceId
    if (source != 0) {
      val messageSource = event.source
      if (messageSource is ServerConnection) {
        putSink(source, messageSource.player.uniqueId)
      }
    }
    forward(header, inputStream)
  }

  override fun sendDirect(header: NetworkHeader, data: ByteArray, sinkUUID: UUID?): Boolean {
    val target = header.path.targetId
    if (sinkUUID != null) {
      if (proxyServer.getPlayer(sinkUUID).flatMap { it.currentServer }
          .filter { it.sendPluginMessage(CHANNEL, data) }
          .isPresent
      ) {
        // we *could* add sinkUUID to the sinks for this connection
        // however, we'll just wait until we receive a response from this server before doing that
        logSent(header, data)
        return true
      }
    }
    if (target != 0) {
      val sinks = getSinks(target)
      while (sinks.hasNext()) {
        val userUUID = sinks.next()
        val connection = proxyServer.getPlayer(userUUID).flatMap { it.currentServer }
        if (!connection.isPresent) {
          sinks.remove()
          continue
        }
        if (connection.get().sendPluginMessage(CHANNEL, data)) {
          logSent(header, data)
          return true
        } else {
          sinks.remove()
        }
      }
    }
    // cant find sink for target, just send to all
    return sendToAll(header, data)
  }

  override fun sendToAll(header: NetworkHeader, data: ByteArray): Boolean {
    val source = header.path.sourceId
    val alreadySent: MutableSet<ServerInfo> = mutableSetOf()
    var result = false
    for (player in proxyServer.allPlayers) {
      val optionalConnection = player.currentServer
      if (!optionalConnection.isPresent) {
        continue
      }
      val connection = optionalConnection.get()
      val info = connection.serverInfo
      if (!alreadySent.contains(info) && isNotSink(source, player.uniqueId)
        && connection.sendPluginMessage(CHANNEL, data)
      ) {
        logSent(header, data)
        alreadySent.add(info)
        result = true
      }
    }
    return result
  }
}
