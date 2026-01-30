/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2026 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.command.context

import org.anvilpowered.anvil.command.StringReader

case class StringRange(start: Int, end: Int) {
  val isEmpty: Boolean = start == end
  val length: Int = end - start
  def get(reader: StringReader): String = reader.string.substring(start, end)
  def get(string: String): String = string.substring(start, end)
}

object StringRange {
  def at(pos: Int): StringRange = StringRange(pos, pos)
  def between(start: Int, end: Int): StringRange = StringRange(start, end)
  def encompassing(a: StringRange, b: StringRange): StringRange = StringRange(math.min(a.start, b.start), math.max(a.end, b.end))
}
