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

package org.anvilpowered.anvil.common.anvilnet.packet.data

import com.google.common.base.MoreObjects
import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import org.anvilpowered.anvil.common.anvilnet.communicator.NetworkHeader
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class BaseData(
    input: ByteArrayDataInput?,
) : DataContainer, NetworkData, Preparable {

    /**
     * The creation time of this packet, used to calculate latency
     */
    lateinit var createdUtc: Instant
        private set

    /**
     * The name of the node that sent the packet
     */
    lateinit var nodeName: String
        private set

    @Transient
    private var travelTime = 0

    @Transient
    private val live: Boolean

    init {
        if (input == null) {
            live = false
            travelTime = -1
        } else {
            live = true
            read(input)
        }
    }

    fun getTravelTime(): Int {
        if (travelTime == -1) {
            travelTime = aliveTime
        }
        return travelTime
    }

    val aliveTime: Int
        get() = if (!live) {
            -1
        } else Duration.between(
            createdUtc,
            OffsetDateTime.now(ZoneOffset.UTC).toInstant()
        ).toMillis().toInt()

    override fun write(output: ByteArrayDataOutput) {
        output.writeUTF(nodeName)
        output.writeInstant(createdUtc)
    }

    override fun read(input: ByteArrayDataInput) {
        nodeName = input.readUTF()
        createdUtc = input.readInstant()
        travelTime = -1
    }

    override fun prepare(nodeName: String) {
        this.nodeName = nodeName
        this.createdUtc = OffsetDateTime.now(ZoneOffset.UTC).toInstant()
    }

    override fun updateNetwork(
        header: NetworkHeader,
        broadcastNetwork: BroadcastNetwork,
        pluginMessageNetwork: PluginMessageNetwork,
    ) {
        println("Updating network from received packet $this")
        val pluginMessageNode = pluginMessageNetwork.ensureNode(header.path.source, nodeName)
        println("Updating network from remote node $pluginMessageNode")
        val pluginMessageConnection = pluginMessageNetwork.ensureConnection(pluginMessageNode)

        pluginMessageConnection.update(getTravelTime())
    }

    private val stringRepresentation by lazy {
        MoreObjects.toStringHelper(this)
            .add("createdUtc", createdUtc)
            .add("nodeName", nodeName)
            .add("travelTime", getTravelTime())
            .add("aliveTime", aliveTime)
            .toString()
    }

    override fun toString(): String = stringRepresentation
}
