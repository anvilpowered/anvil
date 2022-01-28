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
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.api.registry.Key
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.core.command.CommonAnvilCommandNode


val anvilAlias by lazy { CommonAnvilCommandNode.ALIAS }

val whitespace = "\\s+".toRegex()

fun appendJoining(delimiter: Any, vararg contents: Any): Component {
    var delim = delimiter
    if (!(delimiter is Component || delimiter is ComponentBuilder<*, *>)) {
        delim = Component.text(delimiter.toString())
    }

    val indexOfLast = contents.size - 1
    val elements = Component.text()

    for (i in 0..indexOfLast) {
        val o = contents[i]
        if (o is Component) {
            elements.append(o)
        } else {
            elements.append(Component.text(o.toString()))
        }
        if (i != indexOfLast) {
            elements.append(Component.text(delim.toString()))
        }
    }
    return elements.build()
}

fun undo(cmd: String): Component {
    return Component.text()
        .append(
            Component.text("[ Undo ]")
                .color(NamedTextColor.LIGHT_PURPLE)
                .hoverEvent(HoverEvent.showText(
                    Component.text()
                        .append(Component.text("Undo this action\n").color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text(cmd).color(NamedTextColor.GRAY))
                        .build()))
                .clickEvent(ClickEvent.runCommand(cmd)))
        .build()
}

fun <T> info(key: Key<T>, registry: Registry): Component {
    return Component.text()
        .append(Component.text("$key > ").color(NamedTextColor.GOLD))
        .append(Component.text("\nValue: ").append(printValueYellow(key, registry[key])).color(NamedTextColor.GRAY))
        .append(Component.text("\nDefault: ").append(printValueYellow(key, registry.getDefault(key))).color(NamedTextColor.GRAY))
        .append(Component.text("\nFallback: ").append(printValueYellow(key, key.fallbackValue)).color(NamedTextColor.GRAY))
        .build()
}

fun <T> printValueGreen(key: Key<T>, value: T?): Component {
    if (value == null) {
        return Component.text("none").color(NamedTextColor.RED)
    }
    val primary = key.toString(value)
    val secondary = value.toString()
    val builder = Component.text().append(Component.text(primary).color(NamedTextColor.GREEN))
    if (primary != secondary) {
        builder.append(Component.text(" ( $secondary )").color(NamedTextColor.GRAY))
    }
    return builder.build()
}

fun <T> printValueYellow(key: Key<T>, value: T?): Component {
    if (value == null) {
        return Component.text("none").color(NamedTextColor.RED)
    }
    val primary = key.toString(value)
    val secondary = value.toString()
    val builder = Component.text().append(Component.text(primary).color(NamedTextColor.YELLOW))
    if (primary != secondary) {
        builder.append(Component.text(" ( $secondary )").color(NamedTextColor.GRAY))
    }
    return builder.build()
}

