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
package org.anvilpowered.anvil.paper.command

import com.google.inject.Inject
import org.anvilpowered.anvil.core.AnvilImpl
import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

class PaperCommandExecuteService : CommandExecuteService {

    @Inject(optional = true)
    private val plugin: Plugin? = null

    private fun executeDirect(command: String) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
    }

    override fun execute(command: String) {
        if (Bukkit.isPrimaryThread()) {
            executeDirect(command)
        } else if (plugin != null) {
            Bukkit.getScheduler().runTask(plugin, Runnable { executeDirect(command) })
        } else {
            AnvilImpl.logger?.error("You must bind org.bukkit.plugin.Plugin to your plugin instance to be able to run commands async")
        }
    }
}
