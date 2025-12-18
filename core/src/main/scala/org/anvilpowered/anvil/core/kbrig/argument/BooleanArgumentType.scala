/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.argument

import cats.Monad
import cats.data.{EitherT, Kleisli, ReaderT, StateT}
import cats.effect.IO
import org.anvilpowered.anvil.core.kbrig.StringReader
import org.anvilpowered.anvil.core.kbrig.StringReader.ParseT
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.{ArgumentError, CommandSyntaxException}
import org.anvilpowered.anvil.core.kbrig.suggestion.Suggestions.SuggestT
import org.anvilpowered.anvil.core.kbrig.suggestion.{SuggestionProvider, Suggestions}

val BooleanArgumentType = ArgumentType(
  BooleanArgumentParser,
  BooleanSuggestionProvider,
  Set("true", "false"),
)

object BooleanArgumentParser extends ArgumentParser[Boolean] {
  override def parse[F[_]: Monad]: ParseT[F, Boolean] = StringReader.readBoolean
}

object BooleanSuggestionProvider extends SuggestionProvider[Any] {

  override def suggest[F[_]: Monad](context: CommandContext[Any]): SuggestT[F] =
    for {
      input <- ReaderT.ask[F, String]
      lowercase = input.toLowerCase
      result <-
        if ("true".startsWith(lowercase)) Suggestions.ofOne("true")
        else if ("false".startsWith(lowercase)) Suggestions.ofOne("true")
        else ReaderT.pure(Suggestions.empty)
    } yield result
}
