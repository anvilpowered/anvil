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
package org.anvilpowered.anvil.sponge.anvilnet.communicator

import org.spongepowered.api.network.ChannelBuf
import java.io.ByteArrayInputStream

class ChannelBufByteArrayInputStream(private val channelBuf: ChannelBuf) : ByteArrayInputStream(EMPTY) {

    companion object {
        private val EMPTY = ByteArray(0)
    }

    init {
        mark(0)
    }

    @Synchronized
    override fun read(): Int {
        return java.lang.Byte.toUnsignedInt(channelBuf.readByte())
    }

    @Synchronized
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        var len = len
        if (off < 0 || len < 0 || len > b.size - off) {
            throw IndexOutOfBoundsException()
        }
        val available = channelBuf.available()
        if (available <= 0) {
            return -1
        }
        if (len > available) {
            len = available
        }
        if (len == 0) {
            return 0
        }
        for (i in off until len) {
            b[i] = channelBuf.readByte()
        }
        return len
    }

    @Synchronized
    override fun skip(n: Long): Long {
        var k = available()
        if (n < k) {
            k = if (n < 0) 0 else n.toInt()
        }
        channelBuf.setReadIndex(channelBuf.readerIndex() + k)
        return k.toLong()
    }

    @Synchronized
    override fun available(): Int {
        return channelBuf.available()
    }

    override fun mark(readAheadLimit: Int) {
        mark = channelBuf.readerIndex()
    }

    @Synchronized
    override fun reset() {
        channelBuf.setReadIndex(mark)
    }
}
