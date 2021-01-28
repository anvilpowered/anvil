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

package org.anvilpowered.anvil.nukkit.command.regedit

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandExecutor
import cn.nukkit.command.CommandSender
import com.google.inject.Singleton
import org.anvilpowered.anvil.common.command.regedit.CommonRegistryEditRootCommand

@Singleton
class NukkitRegistryEditRootCommand
    : CommonRegistryEditRootCommand<Player, Player, String, CommandSender>(), CommandExecutor {
    override fun onCommand(source: CommandSender, command: Command, alias: String, context: Array<String>): Boolean {
        execute(source, context)
        return true
    }
}
