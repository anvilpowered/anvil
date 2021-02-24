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
package org.anvilpowered.anvil.bungee.module

import com.google.inject.TypeLiteral
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.misc.BindingExtensions
import org.anvilpowered.anvil.api.server.LocationService
import org.anvilpowered.anvil.api.util.KickService
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.bungee.command.BungeeCommandExecuteService
import org.anvilpowered.anvil.bungee.command.BungeeSimpleCommandService
import org.anvilpowered.anvil.bungee.server.BungeeLocationService
import org.anvilpowered.anvil.bungee.util.BungeeKickService
import org.anvilpowered.anvil.bungee.util.BungeePermissionService
import org.anvilpowered.anvil.bungee.util.BungeeTextService
import org.anvilpowered.anvil.bungee.util.BungeeUserService
import org.anvilpowered.anvil.common.PlatformImpl
import org.anvilpowered.anvil.common.command.CommonCallbackCommand
import org.anvilpowered.anvil.common.module.JavaUtilLoggingAdapter
import org.anvilpowered.anvil.common.module.PlatformModule

class ApiBungeeModule : PlatformModule(
  PlatformImpl(
    "bungee",
    true,
    { ProxyServer.getInstance().version },
    JavaUtilLoggingAdapter::bindLogger,
  )
) {
  override fun configure() {
    super.configure()
    val callbackCommandType = object : TypeLiteral<CommonCallbackCommand<CommandSender>>() {}
    bind(callbackCommandType).toProvider(BindingExtensions.asInternalProvider(callbackCommandType))
    bind(CommandExecuteService::class.java).to(BungeeCommandExecuteService::class.java)
    bind(object : TypeLiteral<SimpleCommandService<CommandSender>>() {}).to(BungeeSimpleCommandService::class.java)
    bind(KickService::class.java).to(BungeeKickService::class.java)
    bind(LocationService::class.java).to(BungeeLocationService::class.java)
    bind(PermissionService::class.java).to(BungeePermissionService::class.java)
    bind(object : TypeLiteral<TextService<CommandSender>>() {}).to(BungeeTextService::class.java)
    bind(object : TypeLiteral<UserService<ProxiedPlayer, ProxiedPlayer>>() {}).to(BungeeUserService::class.java)
  }
}
