/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.suggestion

import cats.data.{EitherT, Kleisli, ReaderT, StateT}
import cats.effect.{Async, Concurrent, IO}
import cats.kernel.Monoid
import cats.syntax.all.*
import cats.{Applicative, Monad}
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.ArgumentError
import org.anvilpowered.anvil.core.kbrig.suggestion.Suggestions.SuggestT

trait SuggestionProvider[-S] {
  def suggest[F[_]: Async](context: CommandContext[S]): SuggestT[F]
}

object SuggestionProvider {
  val Empty: SuggestionProvider[Any] = new SuggestionProvider[Any] {
    def suggest[F[_]: Async](context: CommandContext[Any]): SuggestT[F] =
      SuggestT.pure(Suggestions.Empty)
  }
}
