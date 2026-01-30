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

import cats.{Applicative, Monad}
import cats.effect.IO
import cats.data.Kleisli
import org.anvilpowered.anvil.command.StringReader.ParseT
import StringArgumentParser.{GreedyPhrase, QuotedPhrase, SingleWord}
import org.anvilpowered.anvil.command.StringReader
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.exception.{ArgumentError, CommandSyntaxException}
import org.anvilpowered.anvil.command.suggestion.SuggestionProvider

object StringArgumentType {
  def singleWord[S](
      suggestionProvider: SuggestionProvider[S] = SuggestionProvider.Empty,
      examples: Set[String] = Set("word", "words_with_underscores"),
  ) = ArgumentType(SingleWord, suggestionProvider, examples)

  def quotedPhrase[S](
      suggestionProvider: SuggestionProvider[S] = SuggestionProvider.Empty,
      examples: Set[String] = Set("\"quoted phrase\"", "word", "\"\""),
  ) = ArgumentType(QuotedPhrase, suggestionProvider, examples)

  def greedyPhrase[S](
      suggestionProvider: SuggestionProvider[S] = SuggestionProvider.Empty,
      examples: Set[String] = Set("word", "words with spaces", "\"and symbols\""),
  ) = ArgumentType(GreedyPhrase, suggestionProvider, examples)
}

object StringArgumentParser {
  object SingleWord extends ArgumentParser[String] {
    override def parse[F[_]: Monad]: ParseT[F, String] = StringReader.readUnquotedString
    override def toString: String = "String.SingleWord"
  }

  object QuotedPhrase extends ArgumentParser[String] {
    override def parse[F[_]: Monad]: ParseT[F, String] = StringReader.readString
    override def toString: String = "String.QuotablePhrase"
  }

  object GreedyPhrase extends ArgumentParser[String] {
    override def parse[F[_]: Monad]: ParseT[F, String] = StringReader.readRemainingString
    override def toString: String = "String.GreedyPhrase"
  }
}
