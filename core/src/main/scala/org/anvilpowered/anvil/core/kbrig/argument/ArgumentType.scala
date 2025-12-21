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
import org.anvilpowered.anvil.core.kbrig.StringReader.ParseT
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.{ArgumentError, CommandError, CommandSyntaxException}
import org.anvilpowered.anvil.core.kbrig.suggestion.{SuggestionProvider, Suggestions}

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
