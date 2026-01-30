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
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.StringReader.ParseT
import org.anvilpowered.anvil.command.exception.{ArgumentError, CommandError, CommandSyntaxException}
import org.anvilpowered.anvil.command.suggestion.{SuggestionProvider, Suggestions}

/** An argument type provides the parsing and suggestion mechanisms for arbitrary user input types.
  *
  * The standard argument types cover the basic numerical and string data types, but it is possible to define custom implementation via this trait.
  *
  * In contrast to Brigadier's ArgumentType interface - which is only parameterized by value type [[T]] - we extend the generic parameters to include the
  * command source type [[S]] in a contravariant position. For most cases, the original generic abstraction is sufficient, and thus we recommend extending
  * `ArgumentType[Any, T]` if there is no dependency a specific source type. Since [[S]] is contravariant, `ArgumentType[Any, T]` can be used anywhere a
  * more specific `ArgumentType[S, T]` is expected.
  *
  * @tparam S
  *   The command source type
  * @tparam T
  *   The target data type
  */
class ArgumentType[-S, T](
    val parser: ArgumentParser[T],
    val suggestionProvider: SuggestionProvider[S],
    val examples: Set[String], // TODO: Custom examples for default argument types are not correctly converted to Brigadier's format
)
