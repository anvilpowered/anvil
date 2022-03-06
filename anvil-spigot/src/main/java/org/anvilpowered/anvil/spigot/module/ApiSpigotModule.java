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

package org.anvilpowered.anvil.spigot.module;

import com.google.inject.TypeLiteral;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.PlatformImpl;
import org.anvilpowered.anvil.api.command.CommandExecuteService;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.anvilpowered.anvil.api.server.LocationService;
import org.anvilpowered.anvil.api.util.KickService;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.anvil.common.command.CommonCallbackCommand;
import org.anvilpowered.anvil.common.entity.EntityUtils;
import org.anvilpowered.anvil.common.module.JavaUtilLoggingAdapter;
import org.anvilpowered.anvil.common.module.PlatformModule;
import org.anvilpowered.anvil.spigot.command.SpigotCallbackCommand;
import org.anvilpowered.anvil.spigot.command.SpigotCommandExecuteService;
import org.anvilpowered.anvil.spigot.command.SpigotCommandService;
import org.anvilpowered.anvil.spigot.entity.SpigotEntityUtils;
import org.anvilpowered.anvil.spigot.server.SpigotLocationService;
import org.anvilpowered.anvil.spigot.util.SpigotKickService;
import org.anvilpowered.anvil.spigot.util.SpigotPermissionService;
import org.anvilpowered.anvil.spigot.util.SpigotTextService;
import org.anvilpowered.anvil.spigot.util.SpigotUserService;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ApiSpigotModule extends PlatformModule {

    public ApiSpigotModule() {
        super(new PlatformImpl(false, "spigot", JavaUtilLoggingAdapter::bindLogger));
    }

    @Override
    protected void configure() {
        super.configure();
        bind(new TypeLiteral<CommonCallbackCommand<TextComponent, CommandSender>>() {
        }).toProvider(BindingExtensions.asInternalProvider(SpigotCallbackCommand.class));
        bind(CommandExecuteService.class).to(SpigotCommandExecuteService.class);
        bind(new TypeLiteral<CommandService<CommandExecutor, CommandSender>>() {
        }).to(SpigotCommandService.class);
        bind(KickService.class).to(SpigotKickService.class);
        bind(EntityUtils.class).to(SpigotEntityUtils.class);
        bind(LocationService.class).to(SpigotLocationService.class);
        bind(PermissionService.class).to(SpigotPermissionService.class);
        bind(new TypeLiteral<TextService<TextComponent, CommandSender>>() {
        }).to(SpigotTextService.class);
        bind(new TypeLiteral<UserService<Player, Player>>() {
        }).to(SpigotUserService.class);
    }
}
