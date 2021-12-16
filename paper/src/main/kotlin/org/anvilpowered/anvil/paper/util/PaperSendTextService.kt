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
import net.kyori.adventure.text.ComponentBuilder
import org.anvilpowered.anvil.common.util.SendTextService
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class PaperSendTextService : SendTextService<CommandSender> {

  override fun CommandSender.send(text: Component) = sendMessage(text)
  override fun ComponentBuilder<*, *>.sendTo(source: CommandSender) = source.sendMessage(build())
  override fun ComponentBuilder<*, *>.sendToConsole() = console.sendMessage(build())
  override val console: CommandSender = Bukkit.getConsoleSender()
}
