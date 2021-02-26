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
package org.anvilpowered.anvil.common.util

import java.net.URL
import java.util.Deque
import java.util.LinkedList
import java.util.function.Consumer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer
import org.anvilpowered.anvil.api.util.TextService

abstract class StringTextService<TCommandSource> : CommonTextService<TCommandSource>() {

  override fun deserialize(text: String): Component = Component.text(text)
  override fun serializePlain(text: Component): String = PlainComponentSerializer.plain().serialize(text)
  override fun serialize(text: Component): String = convertColors(LegacyComponentSerializer.legacySection().serialize(text))

  protected inner class StringTextBuilder : CommonTextBuilder() {
    private val elements: Deque<Any?>

    init {
      elements = LinkedList()
    }

    override fun aqua(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7b"))
      return this
    }

    override fun black(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a70"))
      return this
    }

    override fun blue(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a79"))
      return this
    }

    override fun dark_aqua(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a73"))
      return this
    }

    override fun dark_blue(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a71"))
      return this
    }

    override fun dark_gray(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a78"))
      return this
    }

    override fun dark_green(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a72"))
      return this
    }

    override fun dark_purple(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a75"))
      return this
    }

    override fun dark_red(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a74"))
      return this
    }

    override fun gold(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a76"))
      return this
    }

    override fun gray(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a77"))
      return this
    }

    override fun green(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7a"))
      return this
    }

    override fun light_purple(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7d"))
      return this
    }

    override fun red(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7c"))
      return this
    }

    override fun reset(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7r"))
      return this
    }

    override fun white(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7f"))
      return this
    }

    override fun yellow(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7e"))
      return this
    }

    override fun bold(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7l"))
      return this
    }

    override fun italic(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7o"))
      return this
    }

    override fun obfuscated(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7k"))
      return this
    }

    override fun strikethrough(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7m"))
      return this
    }

    override fun underlined(): TextService.Builder<TCommandSource> {
      elements.add(Component.text("\u00a7n"))
      return this
    }


    override fun append(vararg contents: Any?): TextService.Builder<TCommandSource> {
      for (o in contents) {
        if (o is TextService.Builder<*> || o is Component) {
          elements.add(o)
        } else {
          elements.add(Component.text(o.toString()))
        }
      }
      return this
    }

    override fun appendJoining(delimiter: Any?, vararg contents: Any?): TextService.Builder<TCommandSource> {
      var delem = delimiter
      if (delem is TextService.Builder<*>) {
        delem = delem.build()
      }
      val indexOfLast = contents.size - 1
      for (i in 0..indexOfLast) {
        val o = contents[i]
        if (o is TextService.Builder<*> || o is Component) {
          elements.add(o)
        } else {
          elements.add(Component.text(o.toString()))
        }
        if (i != indexOfLast) {
          elements.add(delem)
        }
      }
      return this
    }

    override fun onHoverShowText(content: Component): TextService.Builder<TCommandSource> {
      return this
    }

    override fun onClickSuggestCommand(command: String): TextService.Builder<TCommandSource> {
      return this
    }

    override fun onClickRunCommand(command: String): TextService.Builder<TCommandSource> {
      return this
    }

    override fun onClickExecuteCallback(callback: Consumer<TCommandSource>): TextService.Builder<TCommandSource> {
      return this
    }

    override fun onClickOpenUrl(url: URL): TextService.Builder<TCommandSource> {
      return this
    }

    override fun onClickOpenUrl(url: String): TextService.Builder<TCommandSource> {
      return this
    }

    override fun build(): Component {
      if (elements.isEmpty()) {
        return Component.empty()
      } else if (elements.size == 1) {
        val o = elements.first
        if (o is TextService.Builder<*>) {
          return (o as TextService.Builder<TCommandSource>).build()
        } else if (o is Component) {
          return o
        }
      }
      val currentBuilder = Component.text()
      for (o in elements) {
        if (o is TextService.Builder<*>) {
          currentBuilder.append((o as TextService.Builder<TCommandSource>).build())
        } else if (o is Component) {
          currentBuilder.append(o as Component?)
        } else if (o is String) {
          currentBuilder.append(Component.text(o.toString()))
        }
      }
      return currentBuilder.build()
    }
  }

  protected fun convertColors(text: String): String {
    var data = text

    if (data.contains("&0")) {
      data = data.replace("&0", "\u00a70")
    }
    if (data.contains("&1")) {
      data = data.replace("&1", "\u00a71")
    }
    if (data.contains("&2")) {
      data = data.replace("&2", "\u00a72")
    }
    if (data.contains("&3")) {
      data = data.replace("&3", "\u00a73")
    }
    if (data.contains("&4")) {
      data = data.replace("&4", "\u00a74")
    }
    if (data.contains("&5")) {
      data = data.replace("&5", "\u00a75")
    }
    if (data.contains("&6")) {
      data = data.replace("&6", "\u00a76")
    }
    if (data.contains("&7")) {
      data = data.replace("&7", "\u00a77")
    }
    if (data.contains("&8")) {
      data = data.replace("&8", "\u00a78")
    }
    if (data.contains("&9")) {
      data = data.replace("&9", "\u00a79")
    }
    if (data.contains("&a")) {
      data = data.replace("&a", "\u00a7a")
    }
    if (data.contains("&b")) {
      data = data.replace("&b","\u00a7b")
    }
    if (data.contains("&d")) {
      data = data.replace("&d", "\u00a7d")
    }
    if (data.contains("&c")) {
      data = data.replace("&c", "\u00a7c")
    }
    if (data.contains("&r")) {
      data = data.replace("&r", "\u00a7r")
    }
    if (data.contains("&f")) {
      data = data.replace("&f", "\u00a7f")
    }
    if (data.contains("&e")) {
      data = data.replace("&e", "\u00a7e")
    }
    if (data.contains("&l")) {
      data = data.replace("&l", "\u00a7l")
    }
    if (data.contains("&o")) {
      data = data.replace("&o", "\u00a7o")
    }
    if (data.contains("&k")) {
      data = data.replace("&k", "\u00a7k")
    }
    if (data.contains("&m")) {
      data = data.replace("&m", "\u00a7m")
    }
    if (data.contains("&n")) {
      data = data.replace("&n", "\ua007n")
    }
    return data
  }
}
