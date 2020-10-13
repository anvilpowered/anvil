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

package org.anvilpowered.anvil.velocity.module;

import com.google.inject.TypeLiteral;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.TextComponent;
import org.anvilpowered.anvil.api.Platform;
import org.anvilpowered.anvil.api.PlatformImpl;
import org.anvilpowered.anvil.api.command.CommandExecuteService;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.server.LocationService;
import org.anvilpowered.anvil.api.util.KickService;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.anvil.common.module.ApiCommonModule;
import org.anvilpowered.anvil.velocity.command.VelocityCommandExecuteService;
import org.anvilpowered.anvil.velocity.command.VelocityCommandService;
import org.anvilpowered.anvil.velocity.server.VelocityLocationService;
import org.anvilpowered.anvil.velocity.util.VelocityKickService;
import org.anvilpowered.anvil.velocity.util.VelocityPermissionService;
import org.anvilpowered.anvil.velocity.util.VelocityTextService;
import org.anvilpowered.anvil.velocity.util.VelocityUserService;

public class ApiVelocityModule extends ApiCommonModule {

    @Override
    protected void configure() {
        super.configure();
        bind(CommandExecuteService.class).to(VelocityCommandExecuteService.class);
        bind(new TypeLiteral<CommandService<Command, CommandSource>>() {
        }).to(VelocityCommandService.class);
        bind(KickService.class).to(VelocityKickService.class);
        bind(LocationService.class).to(VelocityLocationService.class);
        bind(PermissionService.class).to(VelocityPermissionService.class);
        bind(Platform.class).toInstance(new PlatformImpl(true, "velocity"));
        bind(new TypeLiteral<TextService<TextComponent, CommandSource>>() {
        }).to(VelocityTextService.class);
        bind(new TypeLiteral<UserService<Player, Player>>() {
        }).to(VelocityUserService.class);
    }
}
