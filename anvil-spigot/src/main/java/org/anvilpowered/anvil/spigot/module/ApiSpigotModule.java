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

package org.anvilpowered.anvil.spigot.module;

import com.google.inject.TypeLiteral;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.util.KickService;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.TeleportationService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.anvil.common.module.ApiCommonModule;
import org.anvilpowered.anvil.spigot.command.SpigotCommandService;
import org.anvilpowered.anvil.spigot.util.SpigotKickService;
import org.anvilpowered.anvil.spigot.util.SpigotPermissionService;
import org.anvilpowered.anvil.spigot.util.SpigotTextService;
import org.anvilpowered.anvil.spigot.util.SpigotTeleportationService;
import org.anvilpowered.anvil.spigot.util.SpigotUserService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

public class ApiSpigotModule extends ApiCommonModule {

    @Override
    protected void configure() {
        super.configure();
        bind(new TypeLiteral<CommandService<Command, Command, CommandSender>>(){
        }).to(SpigotCommandService.class);
        bind(KickService.class).to(SpigotKickService.class);
        bind(new TypeLiteral<PermissionService<Permissible>>() {
        }).to(SpigotPermissionService.class);
        bind(new TypeLiteral<TextService<TextComponent, CommandSender>>() {
        }).to(SpigotTextService.class);
        bind(TeleportationService.class).to(SpigotTeleportationService.class);
        bind(new TypeLiteral<UserService<Player, Player>>() {
        }).to(SpigotUserService.class);
    }
}
