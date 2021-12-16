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

package org.anvilpowered.anvil.paper.util

import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.util.SendTextService
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PaperSendTextService : SendTextService {

    override fun sendTo(source: Any, component: Component) {
        if (source is CommandSender || source is Player) {
            (source as CommandSender).sendMessage(component)
        }
    }

    override fun sendToConsole(component: Component) {
        Bukkit.getConsoleSender().sendMessage(component)
    }
}
