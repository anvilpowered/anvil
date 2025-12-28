/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.argument

import cats.{Applicative, Monad}
import cats.effect.IO
import cats.data.Kleisli
import org.anvilpowered.anvil.core.kbrig.StringReader
import org.anvilpowered.anvil.core.kbrig.StringReader.ParseT
import org.anvilpowered.anvil.core.kbrig.argument.StringArgumentParser.{GreedyPhrase, QuotedPhrase, SingleWord}
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.{ArgumentError, CommandSyntaxException}
import org.anvilpowered.anvil.core.kbrig.suggestion.SuggestionProvider

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
