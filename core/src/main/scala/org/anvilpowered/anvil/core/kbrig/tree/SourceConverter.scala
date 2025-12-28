package org.anvilpowered.anvil.core.kbrig.tree

import cats.Monad
import cats.effect.{Concurrent, Temporal}
import org.anvilpowered.anvil.core.kbrig.argument.ArgumentType
import org.anvilpowered.anvil.core.kbrig.suggestion.Suggestions.SuggestT
import org.anvilpowered.anvil.core.kbrig.tree.{LiteralCommandNode, RootCommandNode}

/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */

import org.anvilpowered.anvil.core.kbrig.Command
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.suggestion.SuggestionProvider
import org.anvilpowered.anvil.core.kbrig.tree.ArgumentCommandNode

/** Converts the given [ArgumentCommandNode] with source type [S] to an [ArgumentCommandNode] with source type [R].
  */
object SourceConverter {
  extension [S, R, T](original: ArgumentCommandNode[S, T]) {
    def mapSourceArgument(f: R => S): ArgumentCommandNode[R, T] =
      ArgumentCommandNode(
        original.name,
        original.argType.mapSource(f),
        original.command.map(_.mapSource(f)),
        original.requirement.compose(f),
        original.redirect.map(_.mapSource(f)),
        original.forks,
        original.children.view.mapValues(_.mapSource(f)).toMap,
        original.customSuggestions.map(_.mapSource(f)),
      )
  }

  extension [S, R](original: LiteralCommandNode[S]) {

    /** Converts the given [LiteralCommandNode] with source type [S] to an [LiteralCommandNode] with source type [R].
      */
    def mapSourceLiteral(f: R => S): LiteralCommandNode[R] =
      LiteralCommandNode(
        original.name,
        original.command.map(_.mapSource(f)),
        original.requirement.compose(f),
        original.redirect.map(_.mapSource(f)),
        original.forks,
        original.children.view.mapValues(_.mapSource(f)).toMap,
      )
  }

  extension [S, R](original: RootCommandNode[S]) {

    /** Converts the given [RootCommandNode] with source type [S] to an [RootCommandNode] with source type [R].
      */
    def mapSourceRoot(f: R => S): RootCommandNode[R] =
      RootCommandNode(original.children.view.mapValues(_.mapSource(f)).toMap)
  }

  extension [S, R](original: CommandNode[S]) {

    /** Converts the given [CommandNode] with source type [S] to an [CommandNode] with source type [R].
      *
      * Only works for the standard node types:
      *   - [ArgumentCommandNode]
      *   - [LiteralCommandNode]
      *   - [RootCommandNode]
      */
    def mapSource(f: R => S): CommandNode[R] = original match {
      case argument: ArgumentCommandNode[S, ?] => argument.mapSourceArgument(f)
      case literal: LiteralCommandNode[S]      => literal.mapSourceLiteral(f)
      case root: RootCommandNode[S]            => root.mapSourceRoot(f)
      case _                                   => throw UnsupportedOperationException("Command node must be of type [literal, argument, root]")
    }
  }
  extension [S, R](original: SuggestionProvider[S]) {
    def mapSource(f: R => S): SuggestionProvider[R] =
      new SuggestionProvider[R] {
        override def suggest[F[_]: Temporal](context: CommandContext[R]): SuggestT[F] = original.suggest(context.mapToOriginalSource(f))
      }
  }

  extension [S, R, T](original: ArgumentType[S, T]) {
    def mapSource(f: R => S): ArgumentType[R, T] = ArgumentType(original.parser, original.suggestionProvider.mapSource(f), original.examples)
  }

  extension [S, R](original: Command[S]) {
    def mapSource(f: R => S): Command[R] = new Command[R] {
      override def execute[F[_]: Temporal](context: CommandContext[R]): F[Int] = original.execute(context.mapToOriginalSource(f))
    }
  }

  extension [S, R](original: CommandContext[R]) {
    def mapToOriginalSource(f: R => S): CommandContext[S] =
      CommandContext(
        f(original.source),
        original.input,
        original.argumentFetcher,
        original.child.map(_.mapToOriginalSource(f)),
        original.forks,
      )
  }

}
