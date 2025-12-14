/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.context

import org.anvilpowered.anvil.core.kbrig.StringReader

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
