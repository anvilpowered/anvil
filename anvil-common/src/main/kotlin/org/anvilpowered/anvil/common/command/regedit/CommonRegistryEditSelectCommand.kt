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
import kotlin.streams.toList

open class CommonRegistryEditSelectCommand<TUser, TPlayer, TString, TCommandSource>
    : CommonRegistryEditBaseCommand<TUser, TPlayer, TString, TCommandSource>() {

    @Inject
    private lateinit var registryEditRootCommand: CommonRegistryEditRootCommand<TUser, TPlayer, TString, TCommandSource>

    private val usage: TString by lazy {
        textService.builder()
            .append(pluginInfo.prefix)
            .red().append("Please provide exactly one argument!\nUsage: /$alias regedit select <reg>")
            .build()
    }

    open fun execute(source: TCommandSource, context: Array<String>) {
        if (hasNoPerms(source)) return
        val uuid = userService.getUUIDSafe(source)
        val stage = registryEditRootCommand.stages[uuid]
        textService.send(when {
            stage == null -> registryEditRootCommand.notInStage
            context.size == 1 -> stage.setRegistry(context[0])
            else -> usage
        }, source)
    }

    open fun suggest(source: TCommandSource, context: Array<String>): List<String> {
        if (!testPermission(source)) return listOf()
        val stage = registryEditRootCommand.stages[userService.getUUIDSafe(source)] ?: return listOf()
        return when (context.size) {
            1 -> stage.registries.keys.stream().filter { it.startsWith(context[0]) }.toList()
            else -> listOf()
        }
    }
}
