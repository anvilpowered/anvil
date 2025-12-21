/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.suggestion

import cats.data.ReaderT
import cats.effect.{Concurrent, IO}
import cats.effect.IO.asyncForIO
import cats.kernel.Monoid
import cats.syntax.all.*
import cats.{Applicative, Monad, Show}
import fs2.Stream
import org.anvilpowered.anvil.core.kbrig.context.StringRange

case class Suggestions(range: StringRange, list: Seq[Suggestion]) {
  def isEmpty: Boolean = list.isEmpty
}

object Suggestions {
  case class Input(text: String, start: Int) {
    val range: StringRange = StringRange.between(start, text.length)
  }

  /** Models the computation of suggestions from a partial input string.
    */
  type SuggestT[F[_]] = ReaderT[F, Input, Suggestions]
  val SuggestT: ReaderT.type = ReaderT

  val Empty = Suggestions(StringRange.at(0), Seq())

  def ofOne[F[_]: Applicative](text: String, tooltip: Option[String] = None): SuggestT[F] =
    for {
      input <- SuggestT.ask[F, Input]
    } yield
      if (text == input.text) Empty
      else Suggestions(input.range, Seq(Suggestion(input.range, text, tooltip)))

  def of[F[_]: Concurrent, T](
      stream: ReaderT[[X] =>> Stream[F, X], String, T],
      toText: T => String,
      toTooltip: T => Option[String] = { (_: T) => None },
  ): SuggestT[F] = SuggestT { input =>
    stream
      .run(input.text)
      .map { item => Suggestion(input.range, toText(item), toTooltip(item)) }
      .compile
      .toList
      .map { list => create(input.text, list) }
  }

  given Monoid[Suggestions] with {
    def empty: Suggestions = Suggestions.Empty

    def combine(x: Suggestions, y: Suggestions): Suggestions = {
      // Combine based on your logic - here's one approach:
      if (x.isEmpty) y
      else if (y.isEmpty) x
      else {
        // Merge the ranges and combine the suggestion lists
        val newRange = StringRange.between(
          Math.min(x.range.start, y.range.start),
          Math.max(x.range.end, y.range.end),
        )
        Suggestions(newRange, x.list ++ y.list)
      }
    }
  }

  def foo: SuggestT[IO] =
    List(Suggestions.ofOne[IO]("hello"), Suggestions.ofOne[IO]("there")).combineAll

  def create(command: String, suggestions: Seq[Suggestion]): Suggestions = {
    if (suggestions.isEmpty) {
      return Empty
    }
    val range = StringRange(suggestions.map(_.range.start).min, suggestions.map(_.range.end).max)
    Suggestions(range, suggestions.map(_.expand(command, range)).sorted)
  }

  def merge(command: String, suggestions: Seq[Suggestions]): Suggestions = {
    if (suggestions.isEmpty) {
      return Empty
    } else if (suggestions.size == 1) {
      return suggestions.head
    }
    create(command, suggestions.flatMap(_.list))
  }
}
