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

import java.util.UUID

sealed class ForwardingType {

    /**
     * A player's UUID was used to determine the destination of this packet
     */
    data class DirectUUID(val uuid: UUID) : ForwardingType() {
        private val stringRepresentation: String = "DirectUUID{$uuid}"
        override fun toString(): String = stringRepresentation
    }

    /**
     * A direct message meant to be received by this node
     */
    object DirectReceived : ForwardingType()

    /**
     * A direct message meant for another node, to be forwarded
     */
    object DirectForwarded : ForwardingType()

    /**
     * A broadcast message meant for every node
     */
    object Broadcast : ForwardingType()

    val isForMe: Boolean
        get() = this !is DirectForwarded

    override fun toString(): String = javaClass.simpleName
}
