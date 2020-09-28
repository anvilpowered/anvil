/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.nukkit.command.regedit

import cn.nukkit.command.CommandExecutor
import cn.nukkit.command.CommandSender
import com.google.inject.Inject
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.common.command.regedit.CommonRegistryEditCommandNode

class NukkitRegistryEditCommandNode @Inject constructor(
    registry: Registry,
) : CommonRegistryEditCommandNode<CommandExecutor, CommandSender>(registry) {

    @Inject
    private lateinit var registryEditCancelCommand: NukkitRegistryEditCancelCommand

    @Inject
    private lateinit var registryEditCommitCommand: NukkitRegistryEditCommitCommand

    @Inject
    private lateinit var registryEditKeyCommand: NukkitRegistryEditKeyCommand

    @Inject
    private lateinit var registryEditSelectCommand: NukkitRegistryEditSelectCommand

    @Inject
    private lateinit var registryEditStartCommand: NukkitRegistryEditStartCommand

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
