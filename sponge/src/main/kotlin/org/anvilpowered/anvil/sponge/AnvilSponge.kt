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
package org.anvilpowered.anvil.sponge

import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.AnvilImpl
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.EnvironmentBuilderImpl
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode
import org.anvilpowered.anvil.common.module.CommonModule
import org.anvilpowered.anvil.sponge.command.SpongeSimpleCommandService
import org.anvilpowered.anvil.sponge.listener.SpongePlayerListener
import org.anvilpowered.anvil.sponge.module.ApiSpongeModule
import org.anvilpowered.anvil.sponge.module.SpongeFallbackModule
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.lifecycle.LoadedGameEvent
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent
import org.spongepowered.plugin.PluginContainer
import org.spongepowered.plugin.builtin.jvm.Plugin

@Plugin("anvil")
class AnvilSponge @Inject constructor(
    injector: Injector,
) : AnvilImpl(injector, object : CommonModule<CommandCause>("config") {}) {

    @Inject
    private lateinit var plugin: PluginContainer

    private lateinit var registrationEvent: RegisterCommandEvent<Command.Raw>

    @Listener(order = Order.EARLY)
    fun onInit(event: LoadedGameEvent) {
        EnvironmentBuilderImpl.completeInitialization(ApiSpongeModule(), SpongeFallbackModule())
        Sponge.eventManager().registerListeners(
            plugin,
            Anvil.getEnvironment().injector.getInstance(SpongePlayerListener::class.java)
        )
        Anvil.environment.injector.getInstance(SpongeSimpleCommandService::class.java).onRegister(registrationEvent)
    }

    override fun applyToBuilder(builder: Environment.Builder) {
        super.applyToBuilder(builder)
        builder.addEarlyServices(object : TypeLiteral<CommonAnvilCommandNode<User, ServerPlayer, CommandCause>>() {})
    }

    @Listener
    fun onRegister(event: RegisterCommandEvent<Command.Raw>) {
        registrationEvent = event
    }
}
