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

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.api.event.Order
import org.anvilpowered.anvil.api.model.Mappable
import org.anvilpowered.anvil.common.anvilnet.GsonFactory
import java.time.Instant
import java.util.UUID
import kotlin.reflect.KClass

fun <T> ByteArrayDataInput.read(): T? {
  val length = readInt()
  if (length == 0) {
    return null
  }
  val bytes = ByteArray(length)
  for (i in 0 until length) {
    bytes[i] = readByte()
  }
  return Mappable.deserializeUnsafe(bytes)
}

fun ByteArrayDataOutput.write(obj: Any?) {
  if (obj == null) {
    writeInt(0)
    return
  }
  val bytes = Mappable.serializeUnsafe(obj)
  writeInt(bytes.size)
  write(bytes)
}

val gson = GsonFactory.create()

fun <E : Event> ByteArrayDataInput.readAsJson(): E {
  return gson.fromJson(readUTF(), Event::class.java) as E
}

fun ByteArrayDataOutput.writeAsJson(obj: Any) {
  writeUTF(gson.toJson(obj))
}

inline fun <reified T : DataContainer> ByteArrayDataInput.readContainer(): T {
  return T::class.java.getConstructor(ByteArrayDataInput::class.java).newInstance(this)
}

fun ByteArrayDataOutput.writeContainer(dataContainer: DataContainer) {
  dataContainer.write(this)
}

inline fun <reified T : DataContainer> ByteArrayDataInput.readShortContainersAsArray(): Array<T> {
  return Array(readUnsignedShort()) { readContainer() }
}

inline fun <reified T : DataContainer> ByteArrayDataInput.readShortContainersAsList(): MutableList<T> {
  val size = readUnsignedShort()
  val list = ArrayList<T>(size)
  for (i in 0 until size) {
    list.add(readContainer())
  }
  return list
}

fun ByteArrayDataOutput.writeShortContainers(containers: Array<out DataContainer>) {
  writeShort(containers.size)
  containers.forEach(::writeContainer)
}

fun ByteArrayDataOutput.writeShortContainers(containers: List<DataContainer>) {
  writeShort(containers.size)
  containers.forEach(::writeContainer)
}

fun ByteArrayDataInput.readInstant(): Instant {
  return Instant.ofEpochSecond(readLong(), readInt().toLong())
}

fun ByteArrayDataOutput.writeInstant(instant: Instant) {
  writeLong(instant.epochSecond)
  writeInt(instant.nano)
}

fun <T : Any> ByteArrayDataInput.readKClass(): KClass<T> {
  val type = readUTF()
  return Class.forName(type).kotlin as KClass<T>
}

fun ByteArrayDataOutput.writeKClass(clazz: KClass<*>) {
  writeUTF(clazz.java.name)
}

fun ByteArrayDataInput.readOrder(): Order {
  return Order.values()[readUnsignedByte()]
}

fun ByteArrayDataOutput.writeOrder(order: Order) {
  writeByte(order.ordinal)
}

fun ByteArrayDataInput.readUUID(): UUID {
  return UUID(readLong(), readLong())
}

fun ByteArrayDataOutput.writeUUID(uuid: UUID) {
  writeLong(uuid.mostSignificantBits)
  writeLong(uuid.leastSignificantBits)
}
