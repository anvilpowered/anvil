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

import cats.data.ReaderT
import cats.effect.IO.asyncForIO
import cats.effect.{Concurrent, IO}
import cats.kernel.Monoid
import cats.syntax.all.*
import cats.{Applicative, Monad, Show}
import org.anvilpowered.anvil.command.context.StringRange
import fs2.Stream

import scala.annotation.targetName

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

  /** Creates suggestions from a [[Stream]] given the current input.
    */
  def ofStream[F[_]: Concurrent, T](
      stream: ReaderT[[X] =>> Stream[F, X], String, T],
      toText: T => String,
      toTooltip: T => Option[String] = { (_: T) => None },
  ): SuggestT[F] = SuggestT { input =>
    stream
      .run(input.text)
      .map { item => Suggestion(input.range, toText(item), toTooltip(item)) }
      .compile
      .toList
      .map { items => create(input.text, items) }
  }

  /** Creates suggestions from an [[Iterable]] given the current input.
    */
  def ofSeq[F[_]: Concurrent, T](
      iter: ReaderT[F, String, Seq[T]],
      toText: T => String,
      toTooltip: T => Option[String] = { (_: T) => None },
  ): SuggestT[F] = SuggestT { input =>
    iter
      .run(input.text)
      .map { items =>
        items.map { item => Suggestion(input.range, toText(item), toTooltip(item)) }
      }
      .map { items => create(input.text, items) }
  }

  given Monoid[Suggestions] with {
    def empty: Suggestions = Suggestions.Empty

    def combine(x: Suggestions, y: Suggestions): Suggestions = {
      if (x.isEmpty) y
      else if (y.isEmpty) x
      else {
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
