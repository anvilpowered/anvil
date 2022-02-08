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
package org.anvilpowered.anvil.core.command.regedit

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.api.misc.append
import org.anvilpowered.anvil.api.misc.appendCount
import org.anvilpowered.anvil.api.misc.appendIf
import org.anvilpowered.anvil.api.misc.aqua
import org.anvilpowered.anvil.api.misc.dark_gray
import org.anvilpowered.anvil.api.misc.gold
import org.anvilpowered.anvil.api.misc.gray
import org.anvilpowered.anvil.api.misc.green
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.misc.red
import org.anvilpowered.anvil.api.misc.sendTo
import org.anvilpowered.anvil.api.registry.AnvilKeys
import org.anvilpowered.anvil.api.util.SendTextService
import org.anvilpowered.registry.api.ConfigurationService
import org.anvilpowered.registry.api.Registry
import org.anvilpowered.registry.api.key.Key

class Stage<TCommandSource>(
    val envName: String,
    val registries: MutableMap<String, Registry>,
    val pluginInfo: PluginInfo,
    val sendTextService: SendTextService
) {

    lateinit var registry: Pair<String, Registry>

    private val changes: MutableList<Change<*, TCommandSource>> = ArrayList()
    private val availableRegistries: Component by lazy {
        val builder = Component.text()
        // reference equality, we want to check specifically for the same instance
        // if these are the same, don't show [ Internal ] and [ Config ] separately
        if (registries["main"] !== registries["config"]) {
            builder.append(Component.text()
                .append(Component.text("[ Internal ]").color(NamedTextColor.AQUA))
                .hoverEvent(HoverEvent.showText(Component.text()
                    .append(Component.text("The main registry\n").color(NamedTextColor.AQUA))
                    .append(Component.text("/$anvilAlias regedit select internal").color(NamedTextColor.GRAY))
                    .build()
                ))
                .clickEvent(ClickEvent.runCommand("/$anvilAlias regedit select internal"))
                .append(Component.text(" "))
                .build()
            )
        }
        builder.append(Component.text()
            .append(Component.text("[ Config ]").color(NamedTextColor.AQUA))
            .hoverEvent(HoverEvent.showText(Component.text()
                .append(Component.text("The configuration\n").color(NamedTextColor.AQUA))
                .append(Component.text("/$anvilAlias regedit select config").color(NamedTextColor.GRAY))
            ))
            .clickEvent(ClickEvent.runCommand("/$anvilAlias regedit select config"))
            .build()
        )
        for ((name, _) in registries) {
            if (name == "main" || name == "config") {
                continue
            }
            val cmd = "/$anvilAlias regedit select $name"
            builder.append(Component.text()
                .append(Component.text(" "))
                .append(Component.text()
                    .append(Component.text("[ $name ]"))
                    .hoverEvent(HoverEvent.showText(Component.text()
                        .append(Component.text(name).color(NamedTextColor.AQUA))
                        .append(Component.text(cmd).color(NamedTextColor.GRAY))
                        .build()
                    ))
                    .clickEvent(ClickEvent.runCommand(cmd))
                ).build()
            )
        }
        builder.build()
    }


    private val border by lazy {
        Component.text()
            .append(Component.text().appendCount(15, Component.text("=")).dark_gray().build())
            .append(Component.text(" [ ")).dark_gray()
            .append(Component.text("Anvil RegEdit")).gold()
            .append(Component.text(" ] ")).dark_gray()
            .append(Component.text().appendCount(15, Component.text("=")).dark_gray())
            .build()
    }

    private val view by lazy {
        Component.text()
            .append(Component.text("[ Key ]")
                .color(NamedTextColor.AQUA)
                .hoverEvent(HoverEvent.showText(Component.text()
                    .append(Component.text("Key\n").color(NamedTextColor.AQUA))
                    .append(Component.text("/$anvilAlias regedit key <key> [info|set|unset|unstage]")
                        .color(NamedTextColor.GRAY))
                    .build()
                ))
                .clickEvent(ClickEvent.suggestCommand("/$anvilAlias regedit key "))
            ).build()
    }

    private val cancel = Component.text()
        .append(Component.text("[ Cancel ]")
            .color(NamedTextColor.RED)
            .hoverEvent(HoverEvent.showText(Component.text()
                .append(Component.text("Cancel\n").color(NamedTextColor.RED))
                .append(Component.text("/$anvilAlias regedit commit").color(NamedTextColor.GRAY))
                .build()
            ))
            .clickEvent(ClickEvent.runCommand("/$anvilAlias regedit cancel"))
        ).build()

    private val commit = Component.text()
        .append(Component.text("[ Commit ]")
            .color(NamedTextColor.GOLD)
            .hoverEvent(HoverEvent.showText(Component.text()
                .append(Component.text("Commit\n").color(NamedTextColor.GOLD))
                .append(Component.text("/$anvilAlias regedit commit").color(NamedTextColor.GRAY))
                .build()
            ))
            .clickEvent(ClickEvent.runCommand("/$anvilAlias regedit commit"))
        ).build()

    private val noSuchChange = Component.text()
        .append(pluginInfo.prefix)
        .append(Component.text("Could not find change").color(NamedTextColor.RED))
        .build()

    private val selectRegistry = Component.text()
        .append(pluginInfo.prefix)
        .append(Component.text("You must select a registry").color(NamedTextColor.RED))
        .build()

    private val sensitive = Component.text()
        .append(pluginInfo.prefix)
        .append(Component.text("This key is sensitive and may only be viewed or edited if ").color(NamedTextColor.RED))
        .append(Component.text("server.regeditAllowSensitive ").color(NamedTextColor.GOLD))
        .append(Component.text("is enabled in the config").color(NamedTextColor.RED))
        .build()

    private val userImmutable = Component.text()
        .append(pluginInfo.prefix)
        .append(Component.text("This key is user immutable and may not be edited with the regedit command")
            .color(NamedTextColor.RED))
        .build()

    fun setRegistry(name: String?): Component {
        val newReg = when (name) {
            null, "internal", "r", "registry" -> Pair("main", registries["main"])
            "c" -> Pair("config", registries["config"])
            else -> Pair(name, registries[name])
        }
        return if (newReg.second == null) {
            Component.text()
                .append(pluginInfo.prefix)
                .append(Component.text("Could not find registry $name").color(NamedTextColor.RED))
                .build()
        } else {
            registry = Pair(newReg.first, newReg.second!!)
            print()
        }
    }

    fun info(key: Key<*>): Component {
        return when {
            !::registry.isInitialized -> selectRegistry
            else -> when (key.isSensitive(registry.second, AnvilKeys.REGEDIT_ALLOW_SENSITIVE)) {
                true -> sensitive
                false -> info(key, registry.second)
            }
        }
    }

    fun commit(source: TCommandSource): Boolean {
        if (!::registry.isInitialized) {
            selectRegistry.sendTo(source)
            return false
        }
        val reg = registry.second
        if (changes.isEmpty()) {
            Component.text()
                .append(pluginInfo.prefix)
                .append(Component.text("You have no changes!").color(NamedTextColor.RED))
                .sendTo(source)
            return false
        }
        changes.forEach { it.apply(reg) }
        if (reg is ConfigurationService) {
            if (reg.save()) {
                reg.load()
                Component.text()
                    .append(pluginInfo.prefix)
                    .append(Component.text("Successfully committed and saved ").color(NamedTextColor.GREEN))
                    .append(Component.text(changes.size).color(NamedTextColor.GOLD))
                    .append(Component.text(" changes!").color(NamedTextColor.GREEN))
                    .sendTo(source)
            } else {
                Component.text()
                    .append(pluginInfo.prefix)
                    .append(Component.text("There was an error saving the config!")
                        .color(NamedTextColor.RED)
                    ).sendTo(source)
                return false
            }
        } else {
            reg.load()
            Component.text()
                .append(pluginInfo.prefix)
                .append(Component.text("Successfully committed ").color(NamedTextColor.GREEN))
                .append(Component.text(changes.size).color(NamedTextColor.GOLD))
                .append(Component.text(" changes!").color(NamedTextColor.GREEN))
                .sendTo(source)
        }
        return true
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> addChange(key: Key<T>, newValue: T? = null): Component {
        if (!::registry.isInitialized) {
            return selectRegistry
        }
        if (key.isSensitive()) {
            return userImmutable
        }
        if (key.isSensitive(registry.second, AnvilKeys.REGEDIT_ALLOW_SENSITIVE)) {
            return sensitive
        }
        val existing = changes.stream()
            .filter { it.key == key }
            .findAny().orElse(null) as? Change<T, TCommandSource>
        val action: String
        if (existing == null) {
            changes += Change(this, key, newValue)
            action = "added"
        } else {
            existing.newValue = newValue
            action = "edited"
        }
        return print(
            Component.text()
                .append(Component.text("Successfully $action change for ").color(NamedTextColor.GREEN))
                .append(Component.text("$key ${undo("/$anvilAlias regedit key $key unstage")}"))
                .build())
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> unstageChange(key: Key<T>): Component {
        if (!::registry.isInitialized) {
            return selectRegistry
        }
        val index = changes.indexOfFirst { it.key == key }
        if (index == -1) {
            return noSuchChange
        }
        val removed = changes.removeAt(index) as Change<T, TCommandSource>
        return print(
            Component.text()
                .append(Component.text("Successfully unstaged change for ")).green()
                .append(Component.text("$key ${undo("/$anvilAlias regedit key $key set ${key.toString(removed.newValue!!)}")}"))
                .build())
    }

    fun print(message: Component? = null): Component {
        if (!::registry.isInitialized) {
            return Component.text()
                .append(border.append(Component.text("\n\n")))
                .append(Component.text("Started a regedit session for ")).green()
                .append(Component.text("${pluginInfo.name}\n\n")).gold()
                .append(Component.text("Please select one of the following registries:\n\n")
                    .append(availableRegistries)).gray()
                .append(Component.text(" or ").append(cancel).append(Component.text("\n\n").append(border))).gray()
                .build()
        }
        val builder = Component.text()
            .append(border.append(Component.text("\n\n")))
            .appendIf(message != null, message?.append(Component.text("\n\n")) ?: Component.text(""))
            .append(Component.text("[ ${registry.first} ]")).aqua()
            .append(Component.text(" Queued changes:")).gray()

        if (changes.isEmpty()) {
            builder.append(Component.text(" (none)")).red()
        } else {
            builder.append("\n")
            for (change in changes) {
                builder.append(Component.text("\n").append(change.print()))
            }
        }
        return builder.append(Component.text("\n\nPlease select an action:\n\n"))
            .append(view).append(" ")
            .append(commit).append(" ")
            .append(cancel).append("\n\n")
            .append(border)
            .build()
    }
}
