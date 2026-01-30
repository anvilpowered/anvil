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
package org.anvilpowered.anvil.command.argument

import cats.Monad
import cats.data.{EitherT, Kleisli, ReaderT, StateT}
import cats.effect.IO
import org.anvilpowered.anvil.command.StringReader.ParseT
import org.anvilpowered.anvil.command.suggestion.Suggestions.{Input, SuggestT}
import cats.effect.kernel.Async
import org.anvilpowered.anvil.command.StringReader
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.exception.{ArgumentError, CommandSyntaxException}
import org.anvilpowered.anvil.command.suggestion.{SuggestionProvider, Suggestions}

val BooleanArgumentType = ArgumentType(
  BooleanArgumentParser,
  BooleanSuggestionProvider,
  Set("true", "false"),
)

object BooleanArgumentParser extends ArgumentParser[Boolean] {
  override def parse[F[_]: Monad]: ParseT[F, Boolean] = StringReader.readBoolean
}

object BooleanSuggestionProvider extends SuggestionProvider[Any] {

  override def suggest[F[_]: Async](context: CommandContext[Any]): SuggestT[F] =
    for {
      input <- ReaderT.ask[F, Input]
      lowercase = input.text.toLowerCase
      result <-
        if ("true".startsWith(lowercase)) Suggestions.ofOne("true")
        else if ("false".startsWith(lowercase)) Suggestions.ofOne("true")
        else ReaderT.pure(Suggestions.Empty)
    } yield result
}
