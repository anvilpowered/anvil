/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig

import org.anvilpowered.kbrig.context.CommandContext

trait Command[-S] {
  def execute(context: CommandContext[S]): Int
}
