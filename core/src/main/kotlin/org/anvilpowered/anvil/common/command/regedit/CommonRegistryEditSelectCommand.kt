/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.common.command.regedit

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.command.CommandContext
import org.anvilpowered.anvil.api.red
import org.anvilpowered.anvil.api.sendTo
import kotlin.streams.toList

class CommonRegistryEditSelectCommand<TUser, TPlayer, TCommandSource>
    : CommonRegistryEditBaseCommand<TUser, TPlayer, TCommandSource>() {

    @Inject
    private lateinit var registryEditRootCommand: CommonRegistryEditRootCommand<TUser, TPlayer, TCommandSource>

    private val usage by lazy {
        Component.text()
            .append(pluginInfo.prefix)
            .append(Component.text("Please provide exactly one argument!\nUsage: /$anvilAlias regedit select <reg>")).red()
            .build()
    }

    override fun execute(context: CommandContext<TCommandSource>) {
        val uuid = userService.getUUIDSafe(context.source)
        val stage = registryEditRootCommand.stages[uuid]
        when {
            stage == null -> registryEditRootCommand.notInStage
            context.arguments.size == 1 -> stage.setRegistry(context.arguments[0])
            else -> usage
        }.sendTo(context.source)
    }

    override fun suggest(context: CommandContext<TCommandSource>): List<String> {
        val stage = registryEditRootCommand.stages[userService.getUUIDSafe(context.source)] ?: return listOf()
        return when (context.arguments.size) {
            1 -> stage.registries.keys.stream().filter { it.startsWith(context.arguments[0]) }.toList()
            else -> listOf()
        }
    }
}
