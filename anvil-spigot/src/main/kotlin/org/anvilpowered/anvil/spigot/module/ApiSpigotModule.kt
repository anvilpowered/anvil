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
package org.anvilpowered.anvil.spigot.module

import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.misc.BindingExtensions
import org.anvilpowered.anvil.api.server.LocationService
import org.anvilpowered.anvil.api.util.KickService
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.PlatformImpl
import org.anvilpowered.anvil.common.command.CommonCallbackCommand
import org.anvilpowered.anvil.common.entity.EntityUtils
import org.anvilpowered.anvil.common.module.JavaUtilLoggingAdapter
import org.anvilpowered.anvil.common.module.PlatformModule
import org.anvilpowered.anvil.common.util.CommonTextService
import org.anvilpowered.anvil.common.util.SendTextService
import org.anvilpowered.anvil.spigot.command.SpigotCommandExecuteService
import org.anvilpowered.anvil.spigot.command.SpigotSimpleCommandService
import org.anvilpowered.anvil.spigot.entity.SpigotEntityUtils
import org.anvilpowered.anvil.spigot.server.SpigotLocationService
import org.anvilpowered.anvil.spigot.util.SpigotKickService
import org.anvilpowered.anvil.spigot.util.SpigotPermissionService
import org.anvilpowered.anvil.spigot.util.SpigotSendTextService
import org.anvilpowered.anvil.spigot.util.SpigotUserService
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ApiSpigotModule : PlatformModule(
  PlatformImpl(
    "spigot",
    false,
    { Bukkit.getVersion() },
    JavaUtilLoggingAdapter::bindLogger,
  )
) {
  override fun configure() {
    super.configure()
    bind(object : TypeLiteral<TextService<CommandSender>>() {}).to(object : TypeLiteral<CommonTextService<CommandSender>>() {})
    val callbackCommandType: TypeLiteral<CommonCallbackCommand<CommandSender>> =
      object : TypeLiteral<CommonCallbackCommand<CommandSender>>() {}
    bind(callbackCommandType).toProvider(BindingExtensions.asInternalProvider(callbackCommandType))
    bind(CommandExecuteService::class.java).to(SpigotCommandExecuteService::class.java)
    bind(object : TypeLiteral<SimpleCommandService<CommandSender>>() {})
      .to(SpigotSimpleCommandService::class.java)
    bind(KickService::class.java).to(SpigotKickService::class.java)
    bind(EntityUtils::class.java).to(SpigotEntityUtils::class.java)
    bind(LocationService::class.java).to(SpigotLocationService::class.java)
    bind(PermissionService::class.java).to(SpigotPermissionService::class.java)
    bind(object : TypeLiteral<SendTextService<CommandSender>>() {}).to(SpigotSendTextService::class.java)
    bind(object : TypeLiteral<UserService<Player, Player>>() {}).to(SpigotUserService::class.java)
  }
}
