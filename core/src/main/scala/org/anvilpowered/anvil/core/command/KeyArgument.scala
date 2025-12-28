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

package org.anvilpowered.anvil.core.command

import cats.{Applicative, Monad}
import cats.data.{EitherT, ReaderT}
import cats.effect.Temporal
import cats.syntax.all.*
import io.leangen.geantyref.TypeToken
import org.anvilpowered.anvil.core.command.CommandSource.extract
import org.anvilpowered.anvil.core.config.{Key, KeyNamespace}
import org.anvilpowered.anvil.core.kbrig.Command
import org.anvilpowered.anvil.core.kbrig.argument.StringArgumentType
import org.anvilpowered.anvil.core.kbrig.builder.{ArgumentBuilder, RequiredArgumentBuilder}
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.{ArgTypeCastError, CommandError, NotFoundError}
import org.anvilpowered.anvil.core.kbrig.suggestion.{SuggestionProvider, Suggestions}
import org.anvilpowered.anvil.core.kbrig.suggestion.Suggestions.SuggestT

import scala.reflect.ClassTag

object KeyArgument {
  
  extension [S](builder: RequiredArgumentBuilder[S, String]) {
    def suggestKeyArgument(using namespace: KeyNamespace): builder.type =
      builder.suggests(new SuggestionProvider[S] {
        override def suggest[F[_]](context: CommandContext[S])(using F: Temporal[F]): SuggestT[F] =
          Suggestions.ofSeq(ReaderT { prefix =>
            F.pure(namespace.keys.filter(_.name.startsWith(prefix)).toSeq)
          }, _.name)
      })
  }
  
  inline def extract[F[_]: Monad, T](
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
      keyT <- EitherT.cond(
        key.typeToken == typeToken,
        key.asInstanceOf[Key[T]],
        ArgTypeCastError("key", typeToken.getType, key.typeToken.getType, argumentName),
      )
    } yield keyT

  def simpleBuilder[T](
      typeToken: TypeToken[T],
      argumentName: String = "key",
  )(using namespace: KeyNamespace): RequiredArgumentBuilder[CommandSource, Key[T]] =
    ???
}
