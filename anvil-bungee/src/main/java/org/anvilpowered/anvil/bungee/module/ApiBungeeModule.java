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

package org.anvilpowered.anvil.bungee.module;

import com.google.inject.TypeLiteral;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.anvilpowered.anvil.api.Platform;
import org.anvilpowered.anvil.api.PlatformImpl;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.util.KickService;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.anvil.bungee.command.BungeeCommandService;
import org.anvilpowered.anvil.bungee.util.BungeeKickService;
import org.anvilpowered.anvil.bungee.util.BungeePermissionService;
import org.anvilpowered.anvil.bungee.util.BungeeTextService;
import org.anvilpowered.anvil.bungee.util.BungeeUserService;
import org.anvilpowered.anvil.common.module.ApiCommonModule;

import java.util.function.BiConsumer;

public class ApiBungeeModule extends ApiCommonModule {

    @Override
    protected void configure() {
        super.configure();
        bind(new TypeLiteral<CommandService<BiConsumer<CommandSender, String[]>, CommandSender>>(){
        }).to(BungeeCommandService.class);
        bind(KickService.class).to(BungeeKickService.class);
        bind(new TypeLiteral<PermissionService<CommandSender>>() {
        }).to(BungeePermissionService.class);
        bind(Platform.class).toInstance(new PlatformImpl(true, "bungee"));
        bind(new TypeLiteral<TextService<TextComponent, CommandSender>>() {
        }).to(BungeeTextService.class);
        bind(new TypeLiteral<UserService<ProxiedPlayer, ProxiedPlayer>>() {
        }).to(BungeeUserService.class);
    }
}
