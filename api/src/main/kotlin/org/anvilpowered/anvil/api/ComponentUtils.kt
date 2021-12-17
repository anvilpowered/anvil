package org.anvilpowered.anvil.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.util.SendTextService


fun <T> ComponentBuilder<*, *>.sendTo(source: T) {
    Anvil.environment?.injector?.getInstance(SendTextService::class.java)?.send(source, build())
}

fun <T> Component.sendTo(source: T) {
    Anvil.environment?.injector?.getInstance(SendTextService::class.java)?.send(source, this)
}


fun ComponentBuilder<*, *>.appendCount(count: Int, contents: Component): ComponentBuilder<*, *> {
    for (i in 0..count) {
        this.append(contents)
    }
    return this
}

fun ComponentBuilder<*, *>.appendJoining(delimiter: Any, vararg contents: Any): ComponentBuilder<*,*> {
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
    return elements
}

fun ComponentBuilder<*, *>.appendIf(condition: Boolean, contents: Component): ComponentBuilder<*, *> {
    return if(condition) {
        append(contents)
    } else this
}


fun ComponentBuilder<*, *>.append(text: String): ComponentBuilder<*, *> {
    this.append(Component.text(text))
    return this
}


fun ComponentBuilder<*, *>.aqua(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.AQUA)
    return this
}

fun Component.aqua(): Component {
    this.color(NamedTextColor.AQUA)
    return this
}


fun ComponentBuilder<*, *>.black(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.BLACK)
    return this
}

fun Component.black(): Component {
    this.color(NamedTextColor.BLACK)
    return this
}


fun ComponentBuilder<*, *>.blue(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.BLUE)
    return this
}

fun Component.blue(): Component {
    this.color(NamedTextColor.BLUE)
    return this
}


fun ComponentBuilder<*, *>.dark_aqua(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_AQUA)
    return this
}

fun Component.dark_aqua(): Component {
    this.color(NamedTextColor.DARK_AQUA)
    return this
}

fun ComponentBuilder<*, *>.dark_blue(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_BLUE)
    return this
}

fun Component.dark_blue(): Component {
    this.color(NamedTextColor.DARK_BLUE)
    return this
}

fun ComponentBuilder<*, *>.dark_gray(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_GRAY)
    return this
}

fun Component.dark_gray(): Component {
    this.color(NamedTextColor.DARK_GRAY)
    return this
}

fun ComponentBuilder<*, *>.dark_green(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_GREEN)
    return this
}

fun Component.dark_green(): Component {
    this.color(NamedTextColor.DARK_GREEN)
    return this
}

fun ComponentBuilder<*, *>.dark_purple(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_PURPLE)
    return this
}

fun Component.dark_purple(): Component {
    this.color(NamedTextColor.DARK_PURPLE)
    return this
}

fun ComponentBuilder<*, *>.dark_red(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.DARK_RED)
    return this
}

fun Component.dark_red(): Component {
    this.color(NamedTextColor.DARK_RED)
    return this
}

fun ComponentBuilder<*, *>.gold(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.GOLD)
    return this
}

fun Component.gold(): Component {
    this.color(NamedTextColor.GOLD)
    return this
}

fun ComponentBuilder<*, *>.gray(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.GRAY)
    return this
}

fun Component.gray(): Component {
    this.color(NamedTextColor.GRAY)
    return this
}

fun ComponentBuilder<*, *>.green(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.GREEN)
    return this
}

fun Component.green(): Component {
    this.color(NamedTextColor.GREEN)
    return this
}

fun ComponentBuilder<*, *>.light_purple(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.LIGHT_PURPLE)
    return this
}

fun Component.light_purple(): Component {
    this.color(NamedTextColor.LIGHT_PURPLE)
    return this
}

fun ComponentBuilder<*, *>.red(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.RED)
    return this
}

fun Component.red(): Component {
    this.color(NamedTextColor.RED)
    return this
}

fun ComponentBuilder<*, *>.white(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.WHITE)
    return this
}

fun Component.white(): Component {
    this.color(NamedTextColor.WHITE)
    return this
}

fun ComponentBuilder<*, *>.yellow(): ComponentBuilder<*, *> {
    this.color(NamedTextColor.YELLOW)
    return this
}

fun Component.yellow(): Component {
    this.color(NamedTextColor.YELLOW)
    return this
}

fun ComponentBuilder<*, *>.bold(): ComponentBuilder<*, *> {
    this.decorate(TextDecoration.BOLD)
    return this
}
fun Component.bold():Component {
    this.decorate(TextDecoration.BOLD)
    return this
}

fun ComponentBuilder<*, *>.italic(): ComponentBuilder<*, *> {
    this.decorate(TextDecoration.ITALIC)
    return this
}

fun Component.italic(): Component {
    this.decorate(TextDecoration.ITALIC)
    return this
}

fun ComponentBuilder<*, *>.obfuscated(): ComponentBuilder<*, *> {
    this.decorate(TextDecoration.OBFUSCATED)
    return this
}

fun Component.obfuscated(): Component {
    this.decorate(TextDecoration.OBFUSCATED)
    return this
}

fun ComponentBuilder<*, *>.strikethrough(): ComponentBuilder<*, *> {
    this.decorate(TextDecoration.STRIKETHROUGH)
    return this
}

fun Component.strikethrough(): Component {
    this.decorate(TextDecoration.STRIKETHROUGH)
    return this
}

fun ComponentBuilder<*, *>.underlined(): ComponentBuilder<*, *> {
    this.decorate(TextDecoration.UNDERLINED)
    return this
}

fun Component.underlined(): Component {
    this.decorate(TextDecoration.UNDERLINED)
    return this
}
