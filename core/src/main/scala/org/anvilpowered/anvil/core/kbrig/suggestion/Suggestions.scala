/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.suggestion

import cats.Applicative
import cats.data.ReaderT
import cats.kernel.Monoid
import org.anvilpowered.anvil.core.kbrig.context.StringRange

case class Suggestions(range: StringRange, list: Seq[Suggestion]) {
  def isEmpty: Boolean = list.isEmpty
}

object Suggestions {
  val empty = Suggestions(StringRange.at(0), Seq())

  def ofOne[F[_]: Applicative](text: String, tooltip: Option[String] = None): ReaderT[F, String, Suggestions] =
    for {
      input <- ReaderT.ask[F, String]
    } yield
      if (text == input) empty
      else {
        // TODO: Rethink ranges
        val range = StringRange.between(0, input.length)
        Suggestions(range, Seq(Suggestion(range, text, tooltip)))
      }

  def create(command: String, suggestions: Seq[Suggestion]): Suggestions = {
    if (suggestions.isEmpty) {
      return empty
    }
    val range = StringRange(suggestions.map(_.range.start).min, suggestions.map(_.range.end).max)
    Suggestions(range, suggestions.map(_.expand(command, range)).sorted)
  }

  def merge(command: String, suggestions: Seq[Suggestions]): Suggestions = {
    if (suggestions.isEmpty) {
      return empty
    } else if (suggestions.size == 1) {
      return suggestions.head
    }
    create(command, suggestions.flatMap(_.list))
  }
}
