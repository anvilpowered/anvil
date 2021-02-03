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

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.io.ByteArrayDataInput
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.common.anvilnet.communicator.NetworkHeader
import org.anvilpowered.anvil.common.anvilnet.packet.AnvilNetPacket
import org.slf4j.Logger

@Singleton
class CommonPacketTranslator {

    @Inject
    private lateinit var logger: Logger

    private val packetTypes: BiMap<Int, Class<out AnvilNetPacket>> = HashBiMap.create()

    fun registerPacketType(clazz: Class<out AnvilNetPacket>, id: Int? = packetTypesSize++) {
        packetTypes[id] = clazz
    }

    var packetTypesSize = 0
        private set

    fun registerPacketTypeBegin() {
        packetTypesSize = 0
    }

    fun getPacketType(packetClass: Class<out AnvilNetPacket>): Int {
        return packetTypes.inverse()[packetClass]
            ?: throw NoSuchElementException("Packet class " + packetClass.name + " has not been registered!")
    }

    fun parse(header: NetworkHeader, input: ByteArrayDataInput): AnvilNetPacket? {
        val packetClass = packetTypes[header.type]
        if (packetClass == null) {
            logger.error("Could not find packet class for received type ${header.type}")
            return null
        }
        return try {
            packetClass.getConstructor(NetworkHeader::class.java, ByteArrayDataInput::class.java).newInstance(header, input)
        } catch (e: Exception) {
            val className = packetClass.simpleName
            when (e) {
                is IllegalAccessException, is NoSuchMethodException ->
                    logger.error("$className does not have the required (NetworkHeader, ByteArrayDataOutput) constructor", e)
                else -> logger.error("An unknown error occurred while deserializing $className", e)
            }
            null
        }
    }
}
