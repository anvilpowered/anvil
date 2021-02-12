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
import java.util.Random
import java.util.UUID

abstract class PluginMessageCommunicator : BaseCommunicator("PluginMessage", ConnectionType.VERTICAL) {

  protected fun putSink(nodeId: Int, userUUID: UUID) {
    val connectionRef = pluginMessageNetwork.getConnectionRefTo(nodeId)
    pluginMessageNetwork.connectionRefs.forEach {
      if (it.connection.userUUIDs.remove(userUUID)) {
        logger.info("Moving $userUUID from $it to ${connectionRef.toStringCompact()}")
      }
    }
    connectionRef.connection.userUUIDs.add(userUUID)
    logger.info("Added $userUUID to ${connectionRef.toStringCompact()}")
  }

  protected fun remove(nodeId: Int, userUUID: UUID) {
    val connectionRef = pluginMessageNetwork.getConnectionRefTo(nodeId)
    connectionRef.connection.userUUIDs.remove(userUUID)
    logger.info("Removed $userUUID to ${connectionRef.toStringCompact()}")
  }

  protected fun getSinks(nodeId: Int): MutableIterator<UUID> {
    return object : MutableIterator<UUID> {
      var lastIndex = -1
      val userUUIDs = pluginMessageNetwork.getConnectionRefTo(nodeId).connection.userUUIDs

      override fun hasNext(): Boolean = userUUIDs.isNotEmpty()

      override fun next(): UUID {
        lastIndex = Random().nextInt(userUUIDs.size)
        return userUUIDs[lastIndex]
      }

      override fun remove() {
        require(lastIndex != -1)
        userUUIDs.removeAt(lastIndex)
      }
    }
  }

  protected fun isNotSink(nodeId: Int, uuid: UUID): Boolean {
    return !pluginMessageNetwork.getConnectionRefTo(nodeId).connection.userUUIDs.contains(uuid)
  }

  protected fun forward(header: NetworkHeader, inputStream: ByteArrayInputStream) {
    val data = getData(header, inputStream)
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
