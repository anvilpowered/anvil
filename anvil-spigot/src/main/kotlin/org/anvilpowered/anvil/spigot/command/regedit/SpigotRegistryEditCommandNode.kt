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

package org.anvilpowered.anvil.spigot.command.regedit

import com.google.inject.Inject
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.common.command.regedit.CommonRegistryEditCommandNode
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class SpigotRegistryEditCommandNode @Inject constructor(
    registry: Registry,
) : CommonRegistryEditCommandNode<CommandExecutor, CommandSender>(registry) {

    @Inject
    private lateinit var registryEditCancelCommand: SpigotRegistryEditCancelCommand

    @Inject
    private lateinit var registryEditCommitCommand: SpigotRegistryEditCommitCommand

    @Inject
    private lateinit var registryEditKeyCommand: SpigotRegistryEditKeyCommand

    @Inject
    private lateinit var registryEditSelectCommand: SpigotRegistryEditSelectCommand

    @Inject
    private lateinit var registryEditStartCommand: SpigotRegistryEditStartCommand

    val subCommands: MutableMap<List<String>, CommandExecutor> = HashMap()

    override fun loadCommands() {
        subCommands[CANCEL_ALIAS] = registryEditCancelCommand
        subCommands[COMMIT_ALIAS] = registryEditCommitCommand
        subCommands[KEY_ALIAS] = registryEditKeyCommand
        subCommands[SELECT_ALIAS] = registryEditSelectCommand
        subCommands[START_ALIAS] = registryEditStartCommand
        subCommands[HELP_ALIAS] = commandService.generateHelpCommand(this)
    }
}
