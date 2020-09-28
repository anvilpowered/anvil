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

import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.common.command.regedit.CommonRegistryEditCommandNode
import org.anvilpowered.anvil.common.command.regedit.CommonRegistryEditKeyCommand
import org.anvilpowered.anvil.common.command.regedit.alias
import org.anvilpowered.anvil.common.command.regedit.whitespace
import org.spongepowered.api.command.CommandCallable
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.text.Text
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.Optional

class SpongeRegistryEditKeyCommand : CommonRegistryEditKeyCommand<User, Player, Text, CommandSource>(), CommandCallable {

    companion object {
        val DESCRIPTION: Optional<Text> = Optional.of(Text.of(CommonRegistryEditCommandNode.KEY_DESCRIPTION))
        val USAGE: Text get() = Text.of("/$alias regedit key ${CommonRegistryEditCommandNode.KEY_USAGE}")
    }

    override fun process(source: CommandSource, context: String): CommandResult {
        execute(source, context.split(whitespace).toTypedArray())
        return CommandResult.success()
    }

    override fun getSuggestions(source: CommandSource, context: String, targetPosition: Location<World>?): List<String> {
        return suggest(source, context.split(whitespace).toTypedArray())
    }

    override fun testPermission(source: CommandSource): Boolean {
        return source.hasPermission(registry.getOrDefault(Keys.REGEDIT_PERMISSION))
    }

    override fun getShortDescription(source: CommandSource): Optional<Text> = DESCRIPTION
    override fun getHelp(source: CommandSource): Optional<Text> = DESCRIPTION
    override fun getUsage(source: CommandSource): Text = USAGE
}
