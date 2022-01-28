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
package org.anvilpowered.anvil.core.command

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.command.CommandContext
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.api.misc.gold
import org.anvilpowered.anvil.api.misc.green
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.misc.sendTo
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.core.command.regedit.appendJoining
import java.util.Arrays

class CommonAnvilPluginsCommand<TCommandSource> : SimpleCommand<TCommandSource> {

    @Inject
    private lateinit var permissionService: PermissionService

    @Inject
    private lateinit var registry: Registry

    @Inject
    private lateinit var pluginInfo: PluginInfo

    private val DESCRIPTION: Component = Component.text("Anvil plugins command")

    override fun execute(context: CommandContext<TCommandSource>) {
        val values = Anvil.environmentManager.environments.values
        val mappedValues = values
            .asSequence()
            .map { it.pluginInfo.name }
            .iterator()
        val names = Array(values.size) { mappedValues.next() }
        Arrays.sort(names)
        Component.text()
            .append(pluginInfo.prefix)
            .append(Component.text().append(Component.text("Plugins (${names.size}): ")).gold().build())
            .append(Component.text().append(appendJoining(", ", *names)).green().build())
            .sendTo(context.source)
    }

    override fun canExecute(source: TCommandSource): Boolean {
        return permissionService.hasPermission(source, registry.getOrDefault(Keys.PLUGINS_PERMISSION))
    }

    override fun shortDescription(source: TCommandSource): Component = DESCRIPTION
}
