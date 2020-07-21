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

package org.anvilpowered.anvil.bungee.module;

import com.google.inject.TypeLiteral;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.anvilpowered.anvil.api.PlatformImpl;
import org.anvilpowered.anvil.api.command.CommandExecuteService;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.server.LocationService;
import org.anvilpowered.anvil.api.util.KickService;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.anvil.bungee.command.BungeeCommandExecuteService;
import org.anvilpowered.anvil.bungee.command.BungeeCommandService;
import org.anvilpowered.anvil.bungee.server.BungeeLocationService;
import org.anvilpowered.anvil.bungee.util.BungeeKickService;
import org.anvilpowered.anvil.bungee.util.BungeePermissionService;
import org.anvilpowered.anvil.bungee.util.BungeeTextService;
import org.anvilpowered.anvil.bungee.util.BungeeUserService;
import org.anvilpowered.anvil.common.module.JavaUtilLoggingAdapter;
import org.anvilpowered.anvil.common.module.PlatformModule;

import java.util.function.BiConsumer;

public class ApiBungeeModule extends PlatformModule {

    public ApiBungeeModule() {
        super(new PlatformImpl(true, "bungee", JavaUtilLoggingAdapter::bindLogger));
    }

    @Override
    protected void configure() {
        super.configure();
        bind(CommandExecuteService.class).to(BungeeCommandExecuteService.class);
        bind(new TypeLiteral<CommandService<BiConsumer<CommandSender, String[]>, CommandSender>>() {
        }).to(BungeeCommandService.class);
        bind(KickService.class).to(BungeeKickService.class);
        bind(LocationService.class).to(BungeeLocationService.class);
        bind(PermissionService.class).to(BungeePermissionService.class);
        bind(new TypeLiteral<TextService<TextComponent, CommandSender>>() {
        }).to(BungeeTextService.class);
        bind(new TypeLiteral<UserService<ProxiedPlayer, ProxiedPlayer>>() {
        }).to(BungeeUserService.class);
    }
}
