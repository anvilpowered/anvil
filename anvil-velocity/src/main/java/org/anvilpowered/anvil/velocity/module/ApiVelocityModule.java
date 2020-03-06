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

package org.anvilpowered.anvil.velocity.module;

import com.google.inject.TypeLiteral;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.proxy.Player;
import net.kyori.text.TextComponent;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.util.CurrentServerService;
import org.anvilpowered.anvil.api.util.KickService;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.anvil.common.module.ApiCommonModule;
import org.anvilpowered.anvil.velocity.command.VelocityCommandService;
import org.anvilpowered.anvil.velocity.util.VelocityCurrentServerService;
import org.anvilpowered.anvil.velocity.util.VelocityKickService;
import org.anvilpowered.anvil.velocity.util.VelocityPermissionService;
import org.anvilpowered.anvil.velocity.util.VelocityTextService;
import org.anvilpowered.anvil.velocity.util.VelocityUserService;

public class ApiVelocityModule extends ApiCommonModule {

    @Override
    protected void configure() {
        super.configure();
        bind(new TypeLiteral<CommandService<Command, Command, CommandSource>>() {
        }).to(VelocityCommandService.class);
        bind(CurrentServerService.class).to(VelocityCurrentServerService.class);
        bind(KickService.class).to(VelocityKickService.class);
        bind(new TypeLiteral<PermissionService<PermissionSubject>>() {
        }).to(VelocityPermissionService.class);
        bind(new TypeLiteral<TextService<TextComponent, CommandSource>>() {
        }).to(VelocityTextService.class);
        bind(new TypeLiteral<UserService<Player, Player>>() {
        }).to(VelocityUserService.class);
    }
}
