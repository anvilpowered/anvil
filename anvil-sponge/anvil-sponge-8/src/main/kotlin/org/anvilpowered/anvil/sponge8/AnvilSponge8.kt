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
package org.anvilpowered.anvil.sponge8

import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.AnvilImpl
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.EnvironmentBuilderImpl
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode
import org.anvilpowered.anvil.common.module.CommonModule
import org.anvilpowered.anvil.sponge8.command.Sponge8SimpleCommandService
import org.anvilpowered.anvil.sponge8.listener.Sponge8PlayerListener
import org.anvilpowered.anvil.sponge8.module.ApiSponge8Module
import org.anvilpowered.anvil.sponge8.module.Sponge8FallbackModule
import org.spongepowered.api.Server
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent
import org.spongepowered.api.event.lifecycle.LoadedGameEvent
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent
import org.spongepowered.api.event.lifecycle.StartingEngineEvent
import org.spongepowered.plugin.PluginContainer
import org.spongepowered.plugin.builtin.jvm.Plugin

@Plugin("anvil")
class AnvilSponge8 @Inject constructor(
  injector: Injector,
) : AnvilImpl(injector, object : CommonModule<CommandCause>("config") {}) {

  @Inject
  private lateinit var plugin: PluginContainer

  private lateinit var registrationEvent: RegisterCommandEvent<Command.Raw>

  @Listener(order = Order.EARLY)
  fun onInit(event: LoadedGameEvent) {
    EnvironmentBuilderImpl.completeInitialization(ApiSponge8Module(), Sponge8FallbackModule())
    Sponge.eventManager().registerListeners(
      plugin,
      Anvil.getEnvironment().injector.getInstance(Sponge8PlayerListener::class.java)
    )
    Anvil.environment.injector.getInstance(Sponge8SimpleCommandService::class.java).onRegister(registrationEvent)
  }

  override fun applyToBuilder(builder: Environment.Builder) {
    super.applyToBuilder(builder)
    builder.addEarlyServices(object : TypeLiteral<CommonAnvilCommandNode<User, ServerPlayer, CommandCause>>() {})
  }

  @Listener
  fun onRegister(event: RegisterCommandEvent<Command.Raw>){
    registrationEvent = event
  }
}
