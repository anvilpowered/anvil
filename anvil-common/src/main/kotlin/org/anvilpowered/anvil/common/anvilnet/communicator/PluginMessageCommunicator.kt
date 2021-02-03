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
import java.io.ByteArrayInputStream
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.TreeSet
import java.util.UUID

abstract class PluginMessageCommunicator : BaseCommunicator("PluginMessage", ConnectionType.LATERAL) {

    private val sinksOrdered: MutableMap<Int, MutableSet<UUID>>
    private val sinksShuffled: MutableMap<Int, MutableList<UUID>>

    init {
        sinksOrdered = HashMap()
        sinksShuffled = HashMap()
    }

    protected fun putSink(nodeId: Int, userUUID: UUID) {
        var uuidsOrdered = sinksOrdered[nodeId]
        if (uuidsOrdered == null) {
            uuidsOrdered = TreeSet()
            uuidsOrdered.add(userUUID)
            sinksOrdered[nodeId] = uuidsOrdered
            val uuidsShuffled: MutableList<UUID> = ArrayList()
            uuidsShuffled.add(userUUID)
            sinksShuffled[nodeId] = uuidsShuffled
        } else {
            if (uuidsOrdered.add(userUUID)) {
                sinksShuffled[nodeId]!!.add(userUUID)
            }
        }
    }

    protected fun MutableIterator<UUID>.remove(nodeId: Int, userUUID: UUID) {
        logger.info("Removing sink $nodeId for user $userUUID")
        val uuidsOrdered = sinksOrdered[nodeId] ?: return
        if (uuidsOrdered.size == 1) {
            sinksOrdered.remove(nodeId)
            sinksShuffled.remove(nodeId)
        } else if (uuidsOrdered.remove(userUUID)) {
            remove()
        }
    }

    protected fun getSinks(nodeId: Int): MutableIterator<UUID> {
        val uuids = sinksShuffled[nodeId] ?: return Collections.emptyIterator()
        uuids.shuffle()
        return uuids.iterator()
    }

    /**
     * @return `false` only if the provided player is known to be from the provided nodeId
     */
    protected fun isNotSink(nodeId: Int, uuid: UUID): Boolean {
        val uuids = sinksOrdered[nodeId] ?: return true
        return !uuids.contains(uuid)
    }

    protected fun forward(header: NetworkHeader, inputStream: ByteArrayInputStream) {
        val data = getData(header, inputStream)
        //logger.info("[Forward] {} {}", forwardingType, header)
        when (header.path.forwardingType) {
            ForwardingType.DirectForwarded -> sendDirect(header, data)
            ForwardingType.Broadcast -> sendToAll(header, data)
        }
    }

    protected abstract fun sendDirect(header: NetworkHeader, data: ByteArray, sinkUUID: UUID? = null): Boolean
    protected abstract fun sendToAll(header: NetworkHeader, data: ByteArray): Boolean

    override fun send(header: NetworkHeader, data: ByteArray): Boolean {
        return when (val forwardingType = header.path.forwardingType) {
            is ForwardingType.DirectForwarded -> sendDirect(header, data)
            is ForwardingType.DirectUUID -> sendDirect(header, data, forwardingType.uuid)
            else -> sendToAll(header, data)
        }
    }
}
