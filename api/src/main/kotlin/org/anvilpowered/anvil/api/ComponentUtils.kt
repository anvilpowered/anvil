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
package org.anvilpowered.anvil.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.anvilpowered.anvil.api.util.SendTextService


fun <T> ComponentBuilder<*, *>.sendTo(source: T) {
    Anvil.environment?.injector?.getInstance(SendTextService::class.java)?.send(source, build())
}

fun <T> Component.sendTo(source: T) {
    Anvil.environment?.injector?.getInstance(SendTextService::class.java)?.send(source, this)
}

fun ComponentBuilder<*, *>.append(value: String): ComponentBuilder<*, *> {
    this.append(Component.text(value))
    return this
}


fun ComponentBuilder<*, *>.appendCount(count: Int, contents: Component): ComponentBuilder<*, *> {
    for (i in 0..count) {
        this.append(contents)
    }
    return this
}

fun ComponentBuilder<*, *>.appendJoining(delimiter: Any, vararg contents: Any): ComponentBuilder<*, *> {
    var delim = delimiter
    if (!(delimiter is Component || delimiter is ComponentBuilder<*, *>)) {
        delim = Component.text(delimiter.toString())
    }

    val indexOfLast = contents.size - 1
    val elements = this

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
    return elements
}

fun ComponentBuilder<*, *>.appendIf(condition: Boolean, contents: Component): ComponentBuilder<*, *> {
    return if (condition) {
        append(contents)
    } else this
}

fun ComponentBuilder<*, *>.aqua(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.AQUA)
    return this
}

fun ComponentBuilder<*, *>.black(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.BLACK)
    return this
}

fun ComponentBuilder<*, *>.blue(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.BLUE)
    return this
}

fun ComponentBuilder<*, *>.dark_aqua(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_AQUA)
    return this
}

fun ComponentBuilder<*, *>.dark_blue(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_BLUE)
    return this
}

fun ComponentBuilder<*, *>.dark_gray(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_GRAY)
    return this
}

fun ComponentBuilder<*, *>.dark_green(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_GREEN)
    return this
}

fun ComponentBuilder<*, *>.dark_purple(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_PURPLE)
    return this
}

fun ComponentBuilder<*, *>.dark_red(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_RED)
    return this
}

fun ComponentBuilder<*, *>.gold(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.GOLD)
    return this
}

fun ComponentBuilder<*, *>.gray(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.GRAY)
    return this
}

fun ComponentBuilder<*, *>.green(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.GREEN)
    return this
}

fun ComponentBuilder<*, *>.light_purple(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.LIGHT_PURPLE)
    return this
}

fun ComponentBuilder<*, *>.red(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.RED)
    return this
}

fun ComponentBuilder<*, *>.white(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.WHITE)
    return this
}

fun ComponentBuilder<*, *>.yellow(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.YELLOW)
    return this
}

fun ComponentBuilder<*, *>.bold(): ComponentBuilder<*, *> {
    this.decorate(TextDecoration.BOLD)
    return this
}

fun ComponentBuilder<*, *>.italic(): ComponentBuilder<*, *> {
    this.decorate(TextDecoration.ITALIC)
    return this
}

fun ComponentBuilder<*, *>.obfuscated(): ComponentBuilder<*, *> {
    this.decorate(TextDecoration.OBFUSCATED)
    return this
}

fun ComponentBuilder<*, *>.strikethrough(): ComponentBuilder<*, *> {
    this.decorate(TextDecoration.STRIKETHROUGH)
    return this
}

fun ComponentBuilder<*, *>.underlined(): ComponentBuilder<*, *> {
    this.decorate(TextDecoration.UNDERLINED)
    return this
}
