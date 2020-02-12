/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.spigot.util;

import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.md5.util.MD5StringResult;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SpigotStringResult extends MD5StringResult<CommandSender> {

    @Override
    public void send(TextComponent result, CommandSender commandSender) {
        commandSender.spigot().sendMessage(result);
    }

    @Override
    public void sendToConsole(TextComponent result) {
        Bukkit.getConsoleSender().spigot().sendMessage(result);
    }
}
