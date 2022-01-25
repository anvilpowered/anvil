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

package org.anvilpowered.anvil.common.command.regedit

import com.google.inject.Inject
import com.google.inject.name.Named
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.gold
import org.anvilpowered.anvil.api.red
import org.anvilpowered.anvil.api.registry.ConfigurationService
import org.anvilpowered.anvil.api.registry.Registry
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.toList

class CommonRegistryEditStartCommand<TUser, TPlayer, TCommandSource>
    : CommonRegistryEditBaseCommand<TUser, TPlayer, TCommandSource>() {

    @Inject
    private lateinit var registryEditRootCommand: CommonRegistryEditRootCommand<TUser, TPlayer, TCommandSource>

    private val envRegs: MutableMap<String, Map<String, Registry>> = HashMap()

    private val environments: Stream<String> by lazy {
        Anvil.environmentManager
            .environments.values.stream()
            .map { it.name }
            .sorted()
    }

    private val environmentsPrinted: String by lazy {
        Anvil.environmentManager
            .environments.values.stream()
            .map { it.name }
            .sorted().collect(Collectors.joining(", "))
    }


    private fun parseEnv(envName: String): Environment? {
        return Anvil.environmentManager.getEnvironment(envName)
    }

    private fun parseEnv(envName: String, then: (Environment) -> Component): Component {
        return parseEnv(envName)?.let { then(it) }
            ?: Component.text()
                .append(pluginInfo.prefix)
                .append(Component.text("Could not find environment ")).red()
                .append(Component.text(envName)).gold()
                .build()
    }

    private fun Environment.getRegistries(): Map<String, Registry> {
        return envRegs[name] ?: run {
            val registries: MutableMap<String, Registry> = HashMap()
            val main = injector.getInstance(Registry::class.java)
            val config = injector.getInstance(ConfigurationService::class.java)
            for ((key, value) in injector.bindings) {
                if (key.typeLiteral.type == Registry::class) {
                    val reg = value.provider.get() as? Registry ?: continue
                    if (reg !== main && reg !== config) {
                        registries[(key.annotation as? Named)?.value ?: continue] = reg
                    }
                }
            }
            registries["main"] = main
            registries["config"] = config
            envRegs[name] = registries
            return registries
        }
    }

    override fun execute(source: TCommandSource, context: Array<String>) {
        if (context.isEmpty()) {
            sendTextService.send(source,
                Component.text()
                    .append(pluginInfo.prefix)
                    .append(Component.text("Please select an environment. Usage: /$anvilAlias regedit start <env>\n")).red()
                    .append(Component.text("Available environments: $environmentsPrinted")).gold()
                    .build()
            )
            return
        }
        sendTextService.send(
            source,
            parseEnv(context[0]) {
                val newStage = Stage<TCommandSource>(context[0], it.getRegistries().toMutableMap(), it.pluginInfo, sendTextService)
                registryEditRootCommand.stages[userService.getUUIDSafe(source)] = newStage
                newStage.print()
            }
        )
    }

    override fun suggest(source: TCommandSource, context: Array<String>): MutableList<String> {
        return when (context.size) {
            1 -> environments.filter { it.startsWith(context[0]) }.toList() as MutableList
            else -> mutableListOf()
        }
    }
}
