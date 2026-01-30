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
package org.anvilpowered.anvil.command.suggestion

import cats.Show
import cats.implicits.toShow
import org.anvilpowered.anvil.command.context.StringRange

case class Suggestion(
    range: StringRange,
    text: String,
    tooltip: Option[String] = None,
) {
  def apply(input: String): String = {
    if (range.start == 0 && range.end == input.length) {
      return text
    }
    val result = new StringBuilder
    if (range.start > 0) {
      result.append(input.substring(0, range.start))
    }
    result.append(text)
    if (range.end < input.length) {
      result.append(input.substring(range.end))
    }
    result.toString()
  }

  def expand(command: String, range: StringRange): Suggestion = {
    if (range == this.range) {
      return this
    }
    val result = new StringBuilder
    if (range.start < this.range.start) {
      result.append(command.substring(range.start, this.range.start))
    }
    result.append(text)
    if (range.end > this.range.end) {
      result.append(command.substring(this.range.end, range.end))
    }
    Suggestion(range, result.toString(), tooltip)
  }
  override def toString: String = s"Suggestion{range=$range, text='$text', tooltip='$tooltip'}"
}

object Suggestion {
  given ordering: Ordering[Suggestion] = Ordering.by(_.text.toLowerCase)
}
