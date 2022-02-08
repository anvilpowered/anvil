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
package org.anvilpowered.anvil.core.command.regedit

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.api.command.CommandContext
import org.anvilpowered.anvil.api.misc.gold
import org.anvilpowered.anvil.api.misc.red
import org.anvilpowered.anvil.api.registry.AnvilKeys
import org.anvilpowered.anvil.api.misc.sendTo
import org.anvilpowered.registry.api.key.Key
import org.anvilpowered.registry.api.key.Keys
import kotlin.streams.toList

class CommonRegistryEditKeyCommand<TUser, TPlayer, TCommandSource>
    : CommonRegistryEditBaseCommand<TUser, TPlayer, TCommandSource>() {

    @Inject
    private lateinit var registryEditRootCommand: CommonRegistryEditRootCommand<TUser, TPlayer, TCommandSource>

    private val keyActions = listOf("info", "set", "unset", "unstage")

    private val usage =
        Component.text()
            .append(Component.text("Usage: /$anvilAlias regedit key <key> [info|set|unset|unstage] [<value>]")).red()
            .build()

    private val setUsage by lazy {
        Component.text()
            .append(pluginInfo.prefix)
            .append(Component.text("Value required for set subcommand. Usage: /$anvilAlias regedit key <key> set <value>")).red()
            .build()
    }

    private fun unknownKey(keyName: String) =
        Component.text()
            .append(pluginInfo.prefix)
            .append(Component.text("Unknown key: ")).red()
            .append(Component.text(keyName)).gold()
            .build()

    private fun String.resolveKey(envName: String): Key<Any>? {
        return Keys.resolveLocalAndGlobal<Any>(this, envName).orElse(null)
    }

    override fun execute(context: CommandContext<TCommandSource>) {
        val stage = registryEditRootCommand.stages[context.userUUID]
        if (stage == null) {
            registryEditRootCommand.notInStage.sendTo(context.source)
            return
        }
        when (context.arguments.size) {
            0, 1 -> usage
            2 -> when (val key = context.arguments[0].resolveKey(stage.envName)) {
                null -> unknownKey(context.arguments[0])
                else -> when (context.arguments[1]) {
                    "info" -> stage.info(key)
                    "set" -> setUsage
                    "unset" -> stage.addChange(key)
                    "unstage" -> stage.unstageChange(key)
                    else -> usage
                }
            }
            3 -> when (val key = context.arguments[0].resolveKey(stage.envName)) {
                null -> unknownKey(context.arguments[0])
                else -> when (context.arguments[1]) {
                    "set" -> {
                        try {
                            stage.addChange(key, key.parse(context.arguments[2]))
                        } catch (e: Exception) {
                            Component.text()
                                .append(pluginInfo.prefix)
                                .append(Component.text("Error: ${e.message ?: ""}").color(NamedTextColor.RED))
                                .build()
                        }
                    }
                    "info", "unset", "unstage" ->
                        Component.text()
                            .append(pluginInfo.prefix)
                            .append(Component.text("Too many arguments for ${context.arguments[1]}! $usage")
                                .color(NamedTextColor.RED))
                            .build()
                    else -> usage
                }
            }
            else -> usage
        }.sendTo(context.source)
    }

    override fun suggest(context: CommandContext<TCommandSource>): List<String> {
        val stage = registryEditRootCommand.stages[context.userUUID] ?: return listOf()
        return when (context.arguments.size) {
            1 -> Keys.getAll(stage.envName)?.keys?.stream()?.filter { it.startsWith(context.arguments[0]) }?.toList()
                ?: listOf()
            2 -> keyActions.stream().filter { it.startsWith(context.arguments[1]) }.toList()
            else -> listOf()
        }
    }
}
