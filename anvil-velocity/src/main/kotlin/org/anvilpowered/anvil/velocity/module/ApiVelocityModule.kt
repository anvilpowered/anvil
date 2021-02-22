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
package org.anvilpowered.anvil.velocity.module

import com.google.inject.TypeLiteral
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.server.LocationService
import org.anvilpowered.anvil.api.util.KickService
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.PlatformImpl
import org.anvilpowered.anvil.common.module.PlatformModule
import org.anvilpowered.anvil.common.util.CommonTextService
import org.anvilpowered.anvil.common.util.SendTextService
import org.anvilpowered.anvil.velocity.command.VelocityCommandExecuteService
import org.anvilpowered.anvil.velocity.command.VelocitySimpleCommandService
import org.anvilpowered.anvil.velocity.server.VelocityLocationService
import org.anvilpowered.anvil.velocity.util.VelocityKickService
import org.anvilpowered.anvil.velocity.util.VelocityPermissionService
import org.anvilpowered.anvil.velocity.util.VelocitySendTextService
import org.anvilpowered.anvil.velocity.util.VelocityUserService

class ApiVelocityModule : PlatformModule(
  PlatformImpl(
    "velocity",
    true,
    { it.getInstance(ProxyServer::class.java).version.version }
  )
) {
  override fun configure() {
    super.configure()
    bind(CommandExecuteService::class.java).to(VelocityCommandExecuteService::class.java)
    bind(object : TypeLiteral<SimpleCommandService<CommandSource>>() {}).to(VelocitySimpleCommandService::class.java)
    bind(KickService::class.java).to(VelocityKickService::class.java)
    bind(LocationService::class.java).to(VelocityLocationService::class.java)
    bind(PermissionService::class.java).to(VelocityPermissionService::class.java)
    bind(object : TypeLiteral<SendTextService<CommandSource>>() {}).to(VelocitySendTextService::class.java)
    bind(object : TypeLiteral<TextService<CommandSource>>() {}).to(object : TypeLiteral<CommonTextService<CommandSource>>() {})
    bind(object : TypeLiteral<UserService<Player, Player>>() {}).to(VelocityUserService::class.java)
  }
}
