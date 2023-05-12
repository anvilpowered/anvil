/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
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

package org.anvilpowered.anvil.command

import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.domain.command.GameUserCommandScope
import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.anvil.domain.user.Component
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.builder.executesSuspending
import org.anvilpowered.kbrig.context.CommandContext
import org.anvilpowered.kbrig.context.get

context(org.anvilpowered.anvil.domain.datastore.GameUserScope)
class CommonGameUserCommandScope : GameUserCommandScope {
    override fun ArgumentBuilder.Companion.gameUser(
        name: String,
        command: suspend (context: CommandContext<CommandSource>, gameUser: GameUser) -> Int,
    ): RequiredArgumentBuilder<CommandSource, String> =
        required<CommandSource, String>(name, StringArgumentType.SingleWord)
            .suggests { _, builder ->
                GameUser.getAllUserNames(startWith = builder.input).forEach { name -> builder.suggest(name) }
                builder.build()
            }
            .executesSuspending { context ->
                val gameUserName = context.get<String>(name)
                GameUser.findByUsername(gameUserName)?.let { gameUser ->
                    command(context, gameUser)
                } ?: run {
                    context.source.audience.sendMessage(
                        Component.text()
                            .append(Component.text("GameUser with name ", NamedTextColor.RED))
                            .append(Component.text(gameUserName, NamedTextColor.GOLD))
                            .append(Component.text(" not found!", NamedTextColor.RED)),
                    )
                    0
                }
            }
}
