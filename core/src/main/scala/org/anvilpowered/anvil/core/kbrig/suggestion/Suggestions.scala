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
import cats.effect.IO
import cats.kernel.Monoid
import org.anvilpowered.anvil.core.kbrig.context.StringRange
import cats.syntax.all.*

case class Suggestions(range: StringRange, list: Seq[Suggestion]) {
  def isEmpty: Boolean = list.isEmpty
}

object Suggestions {
  /** Models the computation of suggestions from a partial input string.
   */
  type SuggestT[F[_]] = ReaderT[F, String, Suggestions]
  val SuggestT: ReaderT.type = ReaderT
  
  val empty = Suggestions(StringRange.at(0), Seq())

  def ofOne[F[_]: Applicative](text: String, tooltip: Option[String] = None): SuggestT[F] =
    for {
      input <- ReaderT.ask[F, String]
    } yield
      if (text == input) empty
      else {
        // TODO: Rethink ranges
        val range = StringRange.between(0, input.length)
        Suggestions(range, Seq(Suggestion(range, text, tooltip)))
      }

  given Monoid[Suggestions] with {
    def empty: Suggestions = Suggestions.empty

    def combine(x: Suggestions, y: Suggestions): Suggestions = {
      // Combine based on your logic - here's one approach:
      if (x.isEmpty) y
      else if (y.isEmpty) x
      else {
        // Merge the ranges and combine the suggestion lists
        val newRange = StringRange.between(
          Math.min(x.range.start, y.range.start),
          Math.max(x.range.end, y.range.end)
        )
        Suggestions(newRange, x.list ++ y.list)
      }
    }
  }
  

  def foo: SuggestT[IO] =
    List(Suggestions.ofOne[IO]("hello"), Suggestions.ofOne[IO]("there")).combineAll
    
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
