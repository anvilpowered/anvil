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
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.server.LocationService
import org.anvilpowered.anvil.api.util.KickService
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.bungee.command.BungeeCommandExecuteService
import org.anvilpowered.anvil.bungee.command.BungeeSimpleCommandService
import org.anvilpowered.anvil.bungee.server.BungeeLocationService
import org.anvilpowered.anvil.bungee.util.BungeeKickService
import org.anvilpowered.anvil.bungee.util.BungeeMetricService
import org.anvilpowered.anvil.bungee.util.BungeePermissionService
import org.anvilpowered.anvil.bungee.util.BungeeSendTextService
import org.anvilpowered.anvil.bungee.util.BungeeUserService
import org.anvilpowered.anvil.common.PlatformImpl
import org.anvilpowered.anvil.common.metric.MetricService
import org.anvilpowered.anvil.common.module.JavaUtilLoggingAdapter
import org.anvilpowered.anvil.common.module.PlatformModule
import org.anvilpowered.anvil.common.util.CommonTextService
import org.anvilpowered.anvil.common.util.SendTextService

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
    bind(CommandExecuteService::class.java).to(BungeeCommandExecuteService::class.java)
    bind(KickService::class.java).to(BungeeKickService::class.java)
    bind(LocationService::class.java).to(BungeeLocationService::class.java)
    bind(MetricService::class.java).to(BungeeMetricService::class.java)
    bind(PermissionService::class.java).to(BungeePermissionService::class.java)
    bind(object : TypeLiteral<SendTextService<CommandSender>>() {}).to(BungeeSendTextService::class.java)
    bind(object : TypeLiteral<SimpleCommandService<CommandSender>>() {}).to(BungeeSimpleCommandService::class.java)
    bind(object : TypeLiteral<TextService<CommandSender>>() {}).to(object : TypeLiteral<CommonTextService<CommandSender>>() {})
    bind(object : TypeLiteral<UserService<ProxiedPlayer, ProxiedPlayer>>() {}).to(BungeeUserService::class.java)
  }
}
