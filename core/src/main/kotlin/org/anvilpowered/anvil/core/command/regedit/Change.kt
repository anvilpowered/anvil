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
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.registry.key.Key

class Change<T, TCommandSource>(
    private val registry: Registry,
    val key: Key<T>,
    var newValue: T? = null,
) {

    private val remove = Component.text()
        .append(Component.text("[R]")
            .color(NamedTextColor.RED)
            .hoverEvent(HoverEvent.showText(
                Component.text()
                    .append(Component.text("Remove this change\n").color(NamedTextColor.RED))
                    .append(Component.text("/$anvilAlias regedit key $key unstage").color(NamedTextColor.GRAY))
                    .build()
            ))
            .clickEvent(ClickEvent.runCommand("/$anvilAlias regedit $key unstage")))
        .build()

    constructor(stage: Stage<TCommandSource>, key: Key<T>, newValue: T? = null)
        : this(stage.registry.second, key, newValue)

    fun apply(registry: Registry) {
        registry[key] = newValue ?: throw IllegalArgumentException("New value may not be null!")
    }

    fun <T : Comparable<T>> max(a: T, b: T): T {
        return if (a > b) a else b
    }

    fun print(): Component {
        return Component.text()
            .append(Component.text("$remove "))
            .append(Component.text("$key.name > ").color(NamedTextColor.GOLD))
            .append(printValueYellow(key, registry[key]))
            .append(Component.text(" -> ").color(NamedTextColor.GRAY))
            .append(printValueGreen(key, newValue))
            .build()
    }
}
