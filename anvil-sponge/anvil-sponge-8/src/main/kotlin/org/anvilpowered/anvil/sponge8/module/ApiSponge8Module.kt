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
package org.anvilpowered.anvil.sponge8.module

import com.google.inject.TypeLiteral
import net.kyori.adventure.text.Component
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
import org.anvilpowered.anvil.common.module.PlatformModule
import org.anvilpowered.anvil.sponge.entity.SpongeEntityUtils
import org.anvilpowered.anvil.sponge.module.ApiSpongeModule
import org.anvilpowered.anvil.sponge.util.SpongePermissionService
import org.anvilpowered.anvil.sponge8.command.Sponge8CommandExecuteService
import org.anvilpowered.anvil.sponge8.command.Sponge8SimpleCommandService
import org.anvilpowered.anvil.sponge8.server.Sponge8LocationService
import org.anvilpowered.anvil.sponge8.util.Log4jAdapter
import org.anvilpowered.anvil.sponge8.util.Sponge8KickService
import org.anvilpowered.anvil.sponge8.util.Sponge8TextService
import org.anvilpowered.anvil.sponge8.util.Sponge8UserService
import org.spongepowered.api.Platform
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer

class ApiSponge8Module : ApiSpongeModule(
  PlatformImpl(
    "sponge",
    false,
    { Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).metadata.version },
    Log4jAdapter::bindLogger,
  )
) {
  override fun configure() {
    super.configure()
    val callbackCommandType = object : TypeLiteral<CommonCallbackCommand<CommandCause>>() {}
    bind(callbackCommandType).toProvider(BindingExtensions.asInternalProvider(callbackCommandType))
    bind(CommandExecuteService::class.java).to(Sponge8CommandExecuteService::class.java)
    bind(object : TypeLiteral<SimpleCommandService<CommandCause>>() {}).to(Sponge8SimpleCommandService::class.java)
    bind(KickService::class.java).to(Sponge8KickService::class.java)
    bind(LocationService::class.java).to(Sponge8LocationService::class.java)
    bind(object : TypeLiteral<TextService<CommandCause>>() {}).to(Sponge8TextService::class.java)
    bind(object : TypeLiteral<UserService<User, ServerPlayer>>() {}).to(Sponge8UserService::class.java)
  }
}
