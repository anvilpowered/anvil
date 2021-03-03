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
package org.anvilpowered.anvil.sponge7.module

import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.misc.BindingExtensions
import org.anvilpowered.anvil.api.server.LocationService
import org.anvilpowered.anvil.api.util.KickService
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.PlatformImpl
import org.anvilpowered.anvil.common.command.CommonCallbackCommand
import org.anvilpowered.anvil.common.util.SendTextService
import org.anvilpowered.anvil.sponge.module.ApiSpongeModule
import org.anvilpowered.anvil.sponge7.command.Sponge7CommandExecuteService
import org.anvilpowered.anvil.sponge7.command.Sponge7SimpleCommandService
import org.anvilpowered.anvil.sponge7.server.Sponge7LocationService
import org.anvilpowered.anvil.sponge7.util.Sponge7KickService
import org.anvilpowered.anvil.sponge7.util.Sponge7SendTextService
import org.anvilpowered.anvil.sponge7.util.Sponge7UserService
import org.spongepowered.api.Platform
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User

class ApiSponge7Module : ApiSpongeModule(
  PlatformImpl(
    "sponge",
    false,
    { Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).version.orElse("<no version>") },
  )
) {
  override fun configure() {
    super.configure()
    val callbackCommandType = object : TypeLiteral<CommonCallbackCommand<CommandSource>>() {}
    bind(callbackCommandType).toProvider(BindingExtensions.asInternalProvider(callbackCommandType))
    bind(CommandExecuteService::class.java).to(Sponge7CommandExecuteService::class.java)
    bind(object : TypeLiteral<SimpleCommandService<CommandSource>>() {}).to(Sponge7SimpleCommandService::class.java)
    bind(KickService::class.java).to(Sponge7KickService::class.java)
    bind(LocationService::class.java).to(Sponge7LocationService::class.java)
    bind(object : TypeLiteral<SendTextService<CommandSource>>() {}).to(Sponge7SendTextService::class.java)
    bind(object : TypeLiteral<UserService<User, Player>>() {}).to(Sponge7UserService::class.java)
  }
}
