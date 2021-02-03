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

import org.anvilpowered.anvil.common.anvilnet.packet.AnvilNetPacket

interface Communicator {

    /**
     * Sets the listener for messages received by this communicator
     *
     * @param nodeId   The nodeId of this node
     * @param listener The listener to set
     */
    fun setListener(nodeId: Int, listener: ((AnvilNetPacket) -> Unit)?)

    /**
     * Sends a message to the network. This method will block until the message is sent.
     *
     * @param target       The nodeId of the target node (0 for broadcast)
     * @param type         The message type
     * @param packet  The stream containing the message to send
     * @return whether the output was successfully sent to at least one receiver
     */
    fun send(target: Int, type: Int, packet: AnvilNetPacket): Boolean
}
