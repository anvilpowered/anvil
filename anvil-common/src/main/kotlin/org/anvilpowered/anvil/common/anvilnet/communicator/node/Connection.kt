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
package org.anvilpowered.anvil.common.anvilnet.communicator.node

import com.google.common.base.MoreObjects
import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import org.anvilpowered.anvil.common.anvilnet.ConnectionType
import org.anvilpowered.anvil.common.anvilnet.packet.data.DataContainer
import org.anvilpowered.anvil.common.anvilnet.packet.data.writeContainer
import java.io.Serializable
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.ArrayDeque
import java.util.Deque
import java.util.UUID

/**
 * A direct connection between two adjacent nodes
 */
class Connection constructor(
    input: ByteArrayDataInput?,
    nodeA: Node? = null,
    nodeB: Node? = null,
    type: ConnectionType? = null,
    latency: Int? = null,
) : DataContainer, Comparable<Connection>, Serializable {

    companion object {

        /**
         * The maximum number of timeouts before this node is marked as offline
         */
        const val MAX_TIMEOUTS = 5

        /**
         * The maximum number of instant/latency pairs stored by [comLogs]
         */
        const val MAX_COM_LOGS = 10
    }

    private lateinit var comLogs: Deque<Pair<Instant, Int>>
    private var timeouts: Int = 0

    var connected = false
        private set

    /**
     * @return The latency of this connection in milliseconds
     */
    val latency: Int?
        get() = comLogs.peekLast()?.second

    /**
     * @return The most recent communication time of this connection
     */
    val lastCommunicationUtc: Instant?
        get() = comLogs.peekLast()?.first

    /**
     * @return The most recent update time of this connection
     */
    var lastUpdatedUtc: Instant? = null

    /**
     * @return The set of the two nodes on either end of this connection
     */
    lateinit var nodes: Pair<Node, Node>

    lateinit var type: ConnectionType

    private var hashcode: Int? = null

    val userUUIDs: MutableList<UUID> = mutableListOf()

    constructor(input: ByteArrayDataInput) : this(input, null)

    constructor(nodeA: Node, nodeB: Node, type: ConnectionType, latency: Int? = null)
        : this(null, nodeA, nodeB, type, latency)

    init {
        if (input == null) {
            requireNotNull(nodeA) { "nodeA" }
            requireNotNull(nodeB) { "nodeB" }
            requireNotNull(type) { "type" }
            if (latency != null) {
                update(latency)
            }
            setNodes(nodeA, nodeB)
            comLogs = ArrayDeque(MAX_COM_LOGS)
            this.type = type
        } else {
            read(input)
        }
    }

    override fun write(output: ByteArrayDataOutput) {
        output.writeContainer(nodes.first)
        output.writeContainer(nodes.second)
        output.writeByte(type.ordinal)
    }

    override fun read(input: ByteArrayDataInput) {
        setNodes(Node(input), Node(input))
        type = ConnectionType.values()[input.readUnsignedByte()]
    }

    private fun setNodes(nodeA: Node, nodeB: Node, cmp: Int = nodeA.compareTo(nodeB)) {
        require(cmp != 0) { "nodeA cannot be the same as nodeB" }
        nodes = if (cmp < 0) {
            Pair(nodeA, nodeB)
        } else {
            Pair(nodeB, nodeA)
        }
        hashcode = nodeA.hashCode() * nodeB.hashCode()
    }

    private fun updateLastUpdated(): Instant {
        return OffsetDateTime.now(ZoneOffset.UTC).toInstant().also { lastUpdatedUtc = it }
    }

    fun update(latency: Int) {
        require(latency >= 0) { "Latency cannot be negative" }
        while (comLogs.size >= MAX_COM_LOGS) {
            // remove oldest log
            comLogs.pollFirst()
        }
        comLogs.push(Pair(updateLastUpdated(), latency))
        connected = true
    }

    /**
     * Updates this connection with information from a remote connection.
     *
     * This method assumes the caller has already checked that this local connection
     * corresponds to the provided remote connection (e.g. by verifying the nodes)
     *
     * @param connection The remote connection to ingest information from
     */
    fun ingest(connection: Connection) {
        comLogs = connection.comLogs
        timeouts = connection.timeouts
        connected = connection.connected
        updateLastUpdated()
    }

    fun declareConnected() {
        timeouts = 0
        connected = true
    }

    /**
     * @return `true` if this call resulted in this connection going offline
     */
    fun reportTimeout(): Boolean {
        // always increment timeouts, even if already offline
        if (++timeouts > MAX_TIMEOUTS && connected) {
            // this connection just went offline
            connected = false
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else other is Connection && compareTo(other) == 0
    }

    override fun compareTo(other: Connection): Int {
        val cmp = nodes.first.compareTo(other.nodes.first)
        return if (cmp == 0) {
            nodes.second.compareTo(other.nodes.second)
        } else cmp
    }

    override fun hashCode(): Int = hashcode!!

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("nodeA", nodes.first)
            .add("nodeB", nodes.second)
            .add("type", type)
            .toString()
    }
}
