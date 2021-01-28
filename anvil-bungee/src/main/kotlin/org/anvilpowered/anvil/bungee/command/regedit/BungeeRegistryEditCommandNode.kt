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

package org.anvilpowered.anvil.bungee.command.regedit

import com.google.inject.Inject
import net.md_5.bungee.api.CommandSender
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.common.command.regedit.CommonRegistryEditCommandNode
import java.util.function.BiConsumer

class BungeeRegistryEditCommandNode @Inject constructor(
    registry: Registry
) : CommonRegistryEditCommandNode<BiConsumer<CommandSender, Array<String>>, CommandSender>(registry) {

    @Inject
    private lateinit var registryEditCancelCommand: BungeeRegistryEditCancelCommand

    @Inject
    private lateinit var registryEditCommitCommand: BungeeRegistryEditCommitCommand

    @Inject
    private lateinit var registryEditKeyCommand: BungeeRegistryEditKeyCommand

    @Inject
    private lateinit var registryEditSelectCommand: BungeeRegistryEditSelectCommand

    @Inject
    private lateinit var registryEditStartCommand: BungeeRegistryEditStartCommand

    val subCommands: MutableMap<List<String>, BiConsumer<CommandSender, Array<String>>> = HashMap()

    override fun loadCommands() {
        subCommands[CANCEL_ALIAS] = registryEditCancelCommand
        subCommands[COMMIT_ALIAS] = registryEditCommitCommand
        subCommands[KEY_ALIAS] = registryEditKeyCommand
        subCommands[SELECT_ALIAS] = registryEditSelectCommand
        subCommands[START_ALIAS] = registryEditStartCommand
        subCommands[HELP_ALIAS] = commandService.generateHelpCommand(this)
    }
}
