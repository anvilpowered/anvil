/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.builder

import org.anvilpowered.anvil.core.kbrig.tree.LiteralCommandNode

class LiteralArgumentBuilder[S](private val literal: String) extends ArgumentBuilder[S] {
  override def build(): LiteralCommandNode[S] =
    LiteralCommandNode(literal, command, requirement, forward, forks, children)
}
