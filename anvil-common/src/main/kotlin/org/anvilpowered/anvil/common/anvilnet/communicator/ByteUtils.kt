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
package org.anvilpowered.anvil.common.anvilnet.communicator

import java.util.stream.Collectors
import java.util.stream.IntStream

fun Byte.formatHex(): String = String.format("0x%02X", this)
fun Int.formatHex(): String = String.format("0x%08X", this)
fun IntArray.formatHex(): String {
  return "[" + IntStream.of(*this)
    .mapToObj { it.formatHex() }
    .collect(Collectors.joining(", ")) + "]"
}

private val HEX_ARRAY = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
fun ByteArray.formatHex(): String {
  val hexChars = CharArray(size * 2 + (size - 1) / 4)
  for (i in indices) {
    val v: Int = this[i].toInt() and 0xFF
    var h = i * 2 + i / 4
    hexChars[h] = HEX_ARRAY[v ushr 4]
    hexChars[++h] = HEX_ARRAY[v and 0x0F]
    if (i % 4 == 3 && ++h < hexChars.size) {
      hexChars[h] = ' '
    }
  }
  return String(hexChars)
}

fun ByteArray.write(offset: Int, toWrite: Int): Int {
  var offset = offset
  this[offset++] = (toWrite ushr 24).toByte()
  this[offset++] = (toWrite ushr 16).toByte()
  return write(offset, toWrite.toShort())
}

fun ByteArray.write(offset: Int, toWrite: Short): Int {
  var offset = offset
  this[offset++] = (toWrite.toInt() ushr 8).toByte()
  this[offset++] = toWrite.toByte()
  return offset
}

fun ByteArray.write(offset: Int, path: NetworkPath): Int = path.write(this, offset)
