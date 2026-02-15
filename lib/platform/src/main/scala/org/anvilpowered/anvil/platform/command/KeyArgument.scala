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

package org.anvilpowered.anvil.platform.command

import CommandSource.extract
import cats.Applicative
import cats.Monad
import cats.data.EitherT
import cats.data.ReaderT
import cats.effect.Async
import cats.syntax.all.*
import io.leangen.geantyref.TypeToken
import org.anvilpowered.anvil.command.Command
import org.anvilpowered.anvil.command.argument.StringArgumentType
import org.anvilpowered.anvil.command.builder.ArgumentBuilder
import org.anvilpowered.anvil.command.builder.RequiredArgumentBuilder
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.exception.ArgTypeCastError
import org.anvilpowered.anvil.command.exception.CommandError
import org.anvilpowered.anvil.command.exception.NotFoundError
import org.anvilpowered.anvil.command.suggestion.Suggestions
import org.anvilpowered.anvil.config.Key
import org.anvilpowered.anvil.config.KeyNamespace

import scala.reflect.ClassTag
import net.kyori.adventure.text.Component

object KeyArgument {
  extension [S](builder: RequiredArgumentBuilder[S, String]) {
    def suggestKeyArgument(using namespace: KeyNamespace): builder.type =
      builder.suggests([F[_]] =>
        (context: CommandContext[S]) =>
          (F: Async[F]) ?=>
            Suggestions.ofSeq(
              ReaderT { prefix =>
                F.pure(namespace.keys.filter(_.name.startsWith(prefix)).toSeq)
              },
              _.name,
            ),
      )
  }

  inline def extractC[F[_]: Monad, T](
      context: CommandContext[?],
      argumentName: String = "key",
  )(using namespace: KeyNamespace): EitherT[F, CommandError, Key[T]] =
    extract[F, T](Key.typeTokenOf[T], context, argumentName)

  def extract[F[_]: Monad, T](
      typeToken: TypeToken[T],
      context: CommandContext[?],
      argumentName: String,
  )(using namespace: KeyNamespace): EitherT[F, CommandError, Key[T]] =
    for {
      keyName <- context.extract[F, String](argumentName)
      key <- namespace.keys
        .find(_.name == keyName)
        .toOptionT
        .toRight(NotFoundError("key", keyName))
      keyT <- EitherT.cond[F][CommandError, Key[T]](
        key.typeToken == typeToken,
        key.asInstanceOf[Key[T]],
        ArgTypeCastError("key", typeToken.getType, key.typeToken.getType, argumentName),
      )
    } yield keyT

  def extractUntyped[F[_]: Monad](
      context: CommandContext[?],
      argumentName: String = "key",
  )(using namespace: KeyNamespace): EitherT[F, CommandError, Key[?]] =
    for {
      keyName <- context.extract[F, String](argumentName)
      key <- namespace.keys
        .find(_.name == keyName)
        .toOptionT
        .toRight(NotFoundError("key", keyName))
    } yield key

  def simpleBuilder[S, T](
      typeToken: TypeToken[T],
      executes: [F[_]: Monad] => (context: CommandContext[S], key: Key[T]) => EitherT[F, CommandError, Component],
      argumentName: String = "key",
  )(using namespace: KeyNamespace): RequiredArgumentBuilder[S, String] =
    ArgumentBuilder
      .required[S, String](argumentName, StringArgumentType.singleWord())
      .suggestKeyArgument
      .executesCmd(new Command[S] {
        override def execute[F[_]: Async](context: CommandContext[S]): EitherT[F, CommandError, Component] =
          for {
            key <- KeyArgument.extract[F, T](typeToken, context, argumentName)
            result <- executes[F](context, key)
          } yield result
      })

  def simpleBuilderUntyped[S](
      executes: [F[_]: Async] => (context: CommandContext[S], key: Key[?]) => EitherT[F, CommandError, Component],
  )(using namespace: KeyNamespace): RequiredArgumentBuilder[S, String] = simpleBuilderUntyped(executes, "key")

  def simpleBuilderUntyped[S](
      executes: [F[_]: Async] => (context: CommandContext[S], key: Key[?]) => EitherT[F, CommandError, Component],
      argumentName: String,
  )(using namespace: KeyNamespace): RequiredArgumentBuilder[S, String] =
    ArgumentBuilder
      .required[S, String](argumentName, StringArgumentType.singleWord())
      .suggestKeyArgument
      .executesCmd(new Command[S] {
        override def execute[F[_]: Async](context: CommandContext[S]): EitherT[F, CommandError, Component] = {
          for {
            key <- KeyArgument.extractUntyped[F](context, argumentName)
            result <- executes[F](context, key)
          } yield result
        }
      })
}
