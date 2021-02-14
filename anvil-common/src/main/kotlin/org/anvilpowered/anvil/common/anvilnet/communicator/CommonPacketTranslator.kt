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
import org.anvilpowered.anvil.common.anvilnet.packet.AnvilNetPacket
import org.slf4j.Logger
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

@Singleton
class CommonPacketTranslator {

  @Inject
  private lateinit var logger: Logger

  private val packetTypes: BiMap<Int, KClass<out AnvilNetPacket>> = HashBiMap.create()

  var packetTypesSize = AtomicInteger(0)
    private set

  fun registerPacketType(packetType: KClass<out AnvilNetPacket>) {
    packetTypes[packetTypesSize.getAndIncrement()] = packetType
  }

  inline fun <reified T : AnvilNetPacket> registerPacketType() = registerPacketType(T::class)

  fun toPacketType(type: Int): KClass<out AnvilNetPacket> {
    return packetTypes[type]
      ?: throw NoSuchElementException("Packet class $type has not been registered!")
  }

  fun fromPacketType(packetClass: KClass<out AnvilNetPacket>): Int {
    return packetTypes.inverse()[packetClass]
      ?: throw NoSuchElementException("Packet class ${packetClass.simpleName} has not been registered!")
  }

  fun parse(header: NetworkHeader, input: ByteArrayDataInput): AnvilNetPacket? {
    val packetClass = packetTypes[header.type]
    if (packetClass == null) {
      logger.error("Could not find packet class for received type ${header.type}")
      return null
    }
    val parameterTypes = listOf(NetworkHeader::class, ByteArrayDataInput::class)
    val packet = packetClass.constructors
      .find { c -> c.parameters.map { it.type.jvmErasure } == parameterTypes }
      ?.call(header, input)
    if (packet == null) {
      logger.error(
        "${packetClass.simpleName} does not have the required (NetworkHeader, ByteArrayDataOutput) constructor",
        NoSuchMethodException("Could not find matching constructor")
      )
    }
    return packet
  }
}
