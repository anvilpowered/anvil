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
package org.anvilpowered.anvil.sponge.command

import com.google.inject.Inject
import com.google.inject.Singleton
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.command.CommandContext
import org.anvilpowered.anvil.api.command.CommandMapping
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.api.misc.splitContext
import org.anvilpowered.anvil.core.command.CommonSimpleCommandService
import org.anvilpowered.registry.Registry
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.command.CommandCompletion
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.ArgumentReader
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent
import org.spongepowered.plugin.PluginContainer
import java.util.Optional
import java.util.UUID

@Singleton
class SpongeSimpleCommandService @Inject constructor(
    private val plugin: PluginContainer,
    registry: Registry,
) : CommonSimpleCommandService<CommandCause>() {

    private var alreadyLoaded = false

    init {
        registry.whenLoaded {
            if (alreadyLoaded) {
                return@whenLoaded
            }
            alreadyLoaded = true
        }.order(Integer.MIN_VALUE).register()
    }

    private class CommandRegistration(
        val command: Command.Raw,
        val primaryAlias: String,
        vararg val otherAliases: String,
    )

    private inner class PlatformCommand(
        private val delegate: SimpleCommand<CommandCause>,
    ) : Command.Raw {
        override fun process(cause: CommandCause, arguments: ArgumentReader.Mutable): CommandResult {
            val userUUID = if (cause.subject() is ServerPlayer) {
                (cause.subject() as ServerPlayer).uniqueId()
            } else {
                UUID.randomUUID()
            }
            delegate.execute(CommandContext(cause, arguments.remaining().splitContext(), userUUID))
            return CommandResult.success()
        }

        override fun complete(cause: CommandCause, arguments: ArgumentReader.Mutable): MutableList<CommandCompletion> {
            val userUUID = if (cause.subject() is ServerPlayer) {
                (cause.subject() as ServerPlayer).uniqueId()
            } else {
                UUID.randomUUID()
            }
            return delegate.suggest(CommandContext(cause, arguments.remaining().splitContext(), userUUID))
                .map { CommandCompletion.of(it) }
                .toMutableList()
        }

        override fun canExecute(cause: CommandCause): Boolean = delegate.canExecute(cause)
        override fun shortDescription(cause: CommandCause): Optional<Component> = Optional.ofNullable(delegate.shortDescription(cause))
        override fun extendedDescription(cause: CommandCause): Optional<Component> = Optional.ofNullable(delegate.longDescription(cause))
        override fun usage(cause: CommandCause): Component = delegate.usage(cause) ?: Component.empty()
    }

    private val registrations: MutableList<CommandRegistration> = mutableListOf()

    override fun register(mapping: CommandMapping<out SimpleCommand<CommandCause>>) {
        registrations.add(
            CommandRegistration(
                PlatformCommand(mapping.command),
                mapping.name, *mapping.otherAliases.toTypedArray()
            )
        )
    }

    fun onRegister(event: RegisterCommandEvent<Command.Raw>) {
        for (registration in registrations) {
            event.register(plugin, registration.command, registration.primaryAlias, *registration.otherAliases)
        }
    }
}
