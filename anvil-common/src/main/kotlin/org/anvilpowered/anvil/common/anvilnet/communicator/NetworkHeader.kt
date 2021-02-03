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

import com.google.common.base.MoreObjects
import com.google.common.io.ByteArrayDataInput
import org.anvilpowered.anvil.common.anvilnet.ConnectionType

/**
 * Header structure:
 *
 * VERSION (1 byte)
 * SEQUENCE (2 bytes)
 * HOP_COUNT (1 byte)
 * SOURCE (4 bytes, value 0 for unknown)
 * TARGET (4 bytes, value 0 for broadcast)
 * HOPS (4 * HOP_COUNT bytes)
 * TYPE (4 bytes)
 * PAYLOAD_LENGTH (4 bytes)
 *
 */
class NetworkHeader {

    companion object {
        private const val HEADER_BASE_LENGTH: Int = 20
        private const val MESSAGE_VERSION: Byte = 0
    }

    val version: Byte
    val sequence: Short
    val path: NetworkPath
    val type: Int
    val payloadLength: Int

    @Transient
    val length: Int

    @Transient
    val connectionType: ConnectionType

    constructor(sequence: Short, source: Int, target: Int, type: Int, payloadLength: Int, connectionType: ConnectionType) {
        version = MESSAGE_VERSION
        this.sequence = sequence
        path = NetworkPath(source, target)
        this.type = type
        this.payloadLength = payloadLength
        length = HEADER_BASE_LENGTH + 4 * path.hops.size
        this.connectionType = connectionType
    }

    constructor(input: ByteArrayDataInput, nodeId: Int, connectionType: ConnectionType) {
        version = input.readByte()
        check(version == MESSAGE_VERSION) { "Message has unsupported version $version" }
        sequence = input.readShort()
        path = NetworkPath(input, nodeId)
        type = input.readInt()
        payloadLength = input.readInt()
        length = HEADER_BASE_LENGTH + 4 * path.hops.size
        this.connectionType = connectionType
    }

    fun write(data: ByteArray, offset: Int): Int {
        var offset = offset
        data[offset++] = MESSAGE_VERSION
        offset = data.write(offset, sequence)
        offset = data.write(offset, path)
        offset = data.write(offset, type)
        offset = data.write(offset, payloadLength)
        return offset
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("version", version)
            .add("path", path)
            .add("type", type.format())
            .add("length", payloadLength)
            .add("connectionType", connectionType)
            .toString()
    }
}
