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

import org.anvilpowered.anvil.common.anvilnet.ConnectionType
import org.anvilpowered.anvil.common.anvilnet.packet.AnvilNetPacket

abstract class BroadcastCommunicator protected constructor(name: String) : BaseCommunicator(name, ConnectionType.HUB) {
    protected var isAvailable: Boolean = false
    override fun send(target: Int, type: Int, packet: AnvilNetPacket): Boolean {
        return if (!isAvailable) {
            false
        } else super.send(target, type, packet)
    }
}
