/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig

import cats.Applicative
import cats.effect.{Concurrent, Temporal}
import org.anvilpowered.anvil.core.kbrig.context.CommandContext

trait Command[-S] {
  def execute[F[_]: Temporal](context: CommandContext[S]): F[Int]
}
