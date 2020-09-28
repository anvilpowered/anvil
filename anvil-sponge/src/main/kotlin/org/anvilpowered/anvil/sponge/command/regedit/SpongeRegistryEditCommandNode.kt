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

package org.anvilpowered.anvil.sponge.command.regedit

import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.common.command.regedit.CommonRegistryEditCommandNode
import org.spongepowered.api.command.CommandCallable
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import java.util.HashMap

@Singleton
class SpongeRegistryEditCommandNode @Inject constructor(
    registry: Registry,
) : CommonRegistryEditCommandNode<CommandExecutor, CommandSource>(registry) {

    @Inject
    private lateinit var registryEditCancelCommand: SpongeRegistryEditCancelCommand

    @Inject
    private lateinit var registryEditCommitCommand: SpongeRegistryEditCommitCommand

    @Inject
    private lateinit var registryEditKeyCommand: SpongeRegistryEditKeyCommand

    @Inject
    private lateinit var registryEditSelectCommand: SpongeRegistryEditSelectCommand

    @Inject
    private lateinit var registryEditStartCommand: SpongeRegistryEditStartCommand

    val subCommands: MutableMap<List<String>, CommandCallable> = HashMap()

    override fun loadCommands() {
        subCommands[CANCEL_ALIAS] = registryEditCancelCommand
        subCommands[COMMIT_ALIAS] = registryEditCommitCommand
        subCommands[KEY_ALIAS] = registryEditKeyCommand
        subCommands[SELECT_ALIAS] = registryEditSelectCommand
        subCommands[START_ALIAS] = registryEditStartCommand
        subCommands[HELP_ALIAS] = CommandSpec.builder().executor(commandService.generateHelpCommand(this)).build()
    }
}
