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
package org.anvilpowered.anvil.nukkit.module

import cn.nukkit.Nukkit
import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.server.LocationService
import org.anvilpowered.anvil.api.util.KickService
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.PlatformImpl
import org.anvilpowered.anvil.common.entity.EntityUtils
import org.anvilpowered.anvil.common.module.PlatformModule
import org.anvilpowered.anvil.common.util.CommonTextService
import org.anvilpowered.anvil.common.util.SendTextService
import org.anvilpowered.anvil.nukkit.command.NukkitCommandExecuteService
import org.anvilpowered.anvil.nukkit.command.NukkitSimpleCommandService
import org.anvilpowered.anvil.nukkit.entity.NukkitEntityUtils
import org.anvilpowered.anvil.nukkit.server.NukkitLocationService
import org.anvilpowered.anvil.nukkit.util.NukkitKickService
import org.anvilpowered.anvil.nukkit.util.NukkitPermissionService
import org.anvilpowered.anvil.nukkit.util.NukkitSendTextService
import org.anvilpowered.anvil.nukkit.util.NukkitUserService

class ApiNukkitModule : PlatformModule(
  PlatformImpl(
    "nukkit",
    false,
    { Nukkit.API_VERSION },
    NukkitLoggerAdapter::bindLogger,
  )
) {
  override fun configure() {
    super.configure()
    bind(CommandExecuteService::class.java).to(NukkitCommandExecuteService::class.java)
    bind(KickService::class.java).to(NukkitKickService::class.java)
    bind(EntityUtils::class.java).to(NukkitEntityUtils::class.java)
    bind(LocationService::class.java).to(NukkitLocationService::class.java)
    bind(PermissionService::class.java).to(NukkitPermissionService::class.java)
    bind(object : TypeLiteral<SendTextService<CommandSender>>() {}).to(NukkitSendTextService::class.java)
    bind(object : TypeLiteral<SimpleCommandService<CommandSender>>() {}).to(NukkitSimpleCommandService::class.java)
    bind(object : TypeLiteral<TextService<CommandSender>>() {}).to(object : TypeLiteral<CommonTextService<CommandSender>>() {})
    bind(object : TypeLiteral<UserService<Player, Player>>() {}).to(NukkitUserService::class.java)
  }
}
