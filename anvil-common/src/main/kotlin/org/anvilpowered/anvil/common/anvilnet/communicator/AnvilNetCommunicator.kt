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
package org.anvilpowered.anvil.common.anvilnet.communicator

import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.common.anvilnet.packet.AnvilNetPacket

/**
 * This class decides which underlying communicator to use.
 */
@Singleton
class AnvilNetCommunicator : Communicator {

  @Inject
  private lateinit var pluginMessageCommunicator: PluginMessageCommunicator

  @Inject
  private lateinit var redisCommunicator: RedisCommunicator

  override fun setListener(nodeId: Int, listener: ((AnvilNetPacket) -> Unit)?) {
    pluginMessageCommunicator.setListener(nodeId, listener)
    redisCommunicator.setListener(nodeId, listener)
  }

  override fun send(target: Int, type: Int, packet: AnvilNetPacket): Boolean {
    return (redisCommunicator.send(target, type, packet)
      || pluginMessageCommunicator.send(target, type, packet))
  }
}
