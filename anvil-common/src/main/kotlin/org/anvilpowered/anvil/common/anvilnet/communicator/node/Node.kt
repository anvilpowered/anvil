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
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.common.anvilnet.communicator.format
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import org.anvilpowered.anvil.common.anvilnet.packet.data.DataContainer
import java.io.Serializable

/**
 * Low-level node representing a server. One per communication type per server.
 */
class Node(
    input: ByteArrayDataInput?,
    id: Int? = null,
    name: String? = null,
) : DataContainer, Comparable<Node>, Serializable {

    lateinit var name: String
        private set

    var id: Int = 0
        private set

    val uuidData = NodeUUIDData()

    constructor(input: ByteArrayDataInput) : this(input, null)

    constructor(id: Int, name: String? = null) : this(null, id, name)

    init {
        if (input == null) {
            this.id = id!!
            this.name = name!!
        } else {
            read(input)
        }
    }

    override fun write(output: ByteArrayDataOutput) {
        output.writeInt(id)
        output.writeUTF(name)
    }

    override fun read(input: ByteArrayDataInput) {
        id = input.readInt()
        name = input.readUTF()
    }

    private val pluginMessageNetwork by lazy { Anvil.getEnvironment().injector.getInstance(PluginMessageNetwork::class.java) }
    private val broadcastNetwork by lazy { Anvil.getEnvironment().injector.getInstance(BroadcastNetwork::class.java) }

    fun getLateralConnections(): Set<Connection> = pluginMessageNetwork.getConnections(this)
    fun getMedialConnections(): Set<Connection> = broadcastNetwork.getConnections(this)
    fun getChildren(): Set<Node> = pluginMessageNetwork.getChildren(this)
    fun getParent(): Node? = pluginMessageNetwork.getParent(this)

    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else other is Node && id == other.id
    }

    override fun compareTo(other: Node): Int = id.compareTo(other.id)
    override fun hashCode(): Int = id

    private val stringRepresentation: String by lazy {
        MoreObjects.toStringHelper(this)
            .add("id", this.id.format())
            .add("name", this.name)
            .toString()
    }

    override fun toString(): String = stringRepresentation
}
