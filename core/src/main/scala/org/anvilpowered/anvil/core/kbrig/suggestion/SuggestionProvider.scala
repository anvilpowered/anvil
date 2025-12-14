/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.suggestion

import cats.Monad
import cats.data.{EitherT, Kleisli, ReaderT, StateT}
import cats.effect.IO
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.ArgumentError
import org.anvilpowered.anvil.core.kbrig.suggestion.SuggestionProvider.SuggestT

trait SuggestionProvider[-S] {
  def suggest[F[_]: Monad](context: CommandContext[S]): SuggestT[F]
}

object SuggestionProvider {

  /** Models the computation of suggestions from a partial input string.
    */
  type SuggestT[F[_]] = ReaderT[F, String, Suggestions]
  val SuggestT: ReaderT.type = ReaderT

  val empty: SuggestionProvider[Any] = new SuggestionProvider[Any] {
    def suggest[F[_]: Monad](context: CommandContext[Any]): SuggestT[F] =
      SuggestT.pure(Suggestions.empty)
  }
}
