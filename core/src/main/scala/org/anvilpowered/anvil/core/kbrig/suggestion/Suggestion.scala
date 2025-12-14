/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.suggestion

import cats.Show
import cats.implicits.toShow
import org.anvilpowered.anvil.core.kbrig.context.StringRange

case class Suggestion(
    range: StringRange,
    text: String,
    tooltip: Option[String] = null,
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
