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

package org.anvilpowered.anvil.plugin.core.command.gameuser

import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.user.requiresPermission
import org.anvilpowered.anvil.plugin.core.command.common.addHelp
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.tree.LiteralCommandNode

private val children = mapOf(
  "help" to Component.text("Shows this help message"),
  "info" to Component.text("Shows information about a game user"),
)

object GameUserCommand {
  context(AnvilApi)
  fun create(): LiteralCommandNode<CommandSource> =
    ArgumentBuilder.literal<CommandSource>("gameuser")
      .addHelp("anvil gameuser", children)
      .requiresPermission("anvil.agent.gameuser")
      .build()
}
