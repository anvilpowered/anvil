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
import java.util.UUID

// TODO: user DataContainer
class NetworkPath {
  val sourceId: Int
  val targetId: Int
  val hops: IntArray

  @Transient
  private val hopCount: Int

  @Transient
  private val nodeId: Int

  @Transient
  lateinit var forwardingType: ForwardingType

  @Transient
  val sourcePretty: String

  @Transient
  val targetPretty: String

  @Transient
  val hopsPretty: String

  constructor(source: Int, target: Int, hops: IntArray = IntArray(0), nodeId: Int = source) {
    this.sourceId = source
    sourcePretty = source.format()
    this.targetId = target
    targetPretty = target.format()
    hopCount = hops.size
    this.hops = hops
    hopsPretty = hops.format()
    this.nodeId = nodeId
  }

  constructor(input: ByteArrayDataInput, nodeId: Int) {
    val prevHopCount = input.readUnsignedByte()
    sourceId = input.readInt()
    sourcePretty = sourceId.format()
    targetId = input.readInt()
    targetPretty = targetId.format()
    hopCount = prevHopCount + 1
    hops = IntArray(hopCount)
    for (i in 0 until prevHopCount) {
      hops[i] = input.readInt()
    }
    hops[prevHopCount] = nodeId
    hopsPretty = hops.format()
    this.nodeId = nodeId
  }

  fun setForwardingType(userUUID: UUID? = null) {
    forwardingType = when {
      userUUID != null -> ForwardingType.DirectUUID(userUUID)
      targetId == 0 -> ForwardingType.Broadcast
      targetId == nodeId -> ForwardingType.DirectReceived
      else -> ForwardingType.DirectForwarded
    }
  }

  /**
   * Finds the direct path back from this node to the source node
   *
   * @return The direct path back to the source
   */
  fun back(): NetworkPath {
    val revHops = IntArray(hopCount)
    val hopMax = hopCount - 1
    for (i in 0 until hopCount) {
      revHops[i] = hops[hopMax - i]
    }
    return NetworkPath(nodeId, sourceId, revHops, nodeId)
  }

  fun write(data: ByteArray, offset: Int): Int {
    var offset = offset
    data[offset++] = hopCount.toByte()
    offset = data.write(offset, sourceId)
    offset = data.write(offset, targetId)
    for (hop in hops) {
      offset = data.write(offset, hop)
    }
    return offset
  }

  /**
   * Checks whether a message that was sent along this path should be forwarded to the provided nodeId.
   * This is to prevent loops in the network.
   *
   * @return Whether this
   */
  fun shouldForwardTo(nodeId: Int): Boolean {
    return if (hopCount == 0) {
      nodeId != sourceId
    } else hops[hopCount - 1] != nodeId
  }

  override fun toString(): String {
    return MoreObjects.toStringHelper(this)
      .add("source", sourcePretty)
      .add("target", targetPretty)
      .add("forwardingType", forwardingType)
      .add("hops", hopsPretty)
      .toString()
  }
}
