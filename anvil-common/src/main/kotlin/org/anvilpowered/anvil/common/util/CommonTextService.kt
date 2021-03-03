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

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.Platform
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.common.command.CommonCallbackCommand
import org.slf4j.Logger
import java.net.URL
import java.util.ArrayList
import java.util.Deque
import java.util.LinkedList
import java.util.Optional
import java.util.UUID
import java.util.function.Consumer
import java.util.regex.Pattern

abstract class CommonTextService<TCommandSource> : TextService<TCommandSource> {

  companion object {
    private val LINE_BREAK_PATTERN = Pattern.compile("\r\n|\r|\n")
    private val FORMATTING_PATTERN = Pattern.compile("&[0-9a-fklmnor]")
  }

  @Inject
  private lateinit var callbackCommand: CommonCallbackCommand<TCommandSource>

  @Inject
  private lateinit var logger: Logger

  @Inject
  private lateinit var platform: Platform

  @Inject
  private lateinit var pluginInfo: PluginInfo

  override fun builder(): TextService.Builder<TCommandSource> = this.CommonTextBuilder()
  override fun paginationBuilder(): CommonExtendedPaginationBuilder = CommonExtendedPaginationBuilder()
  override fun send(text: Component, receiver: TCommandSource, sourceUUID: UUID) = send(text, receiver)
  override fun send(text: Component, receiver: TCommandSource, source: Any) = send(text, receiver)
  override fun deserializeAmpersand(text: String): Component = LegacyComponentSerializer.legacyAmpersand().deserialize(text)
  override fun deserializeSection(text: String): Component = LegacyComponentSerializer.legacySection().deserialize(text)
  override fun serializeAmpersand(text: Component): String = LegacyComponentSerializer.legacyAmpersand().serialize(text)
  override fun serializeSection(text: Component): String = LegacyComponentSerializer.legacySection().serialize(text)
  override fun serializePlain(text: Component): String = PlainComponentSerializer.plain().serialize(text)
  override fun toPlain(text: String): String = text.replace(FORMATTING_PATTERN.toRegex(), "")

  override fun lineCount(text: Component?): Int {
    if (text == null) {
      return -1
    }
    val s = serializePlain(text)
    if (s.isEmpty()) {
      return 0
    }
    val m = LINE_BREAK_PATTERN.matcher(s)
    var lines = 1
    while (m.find()) {
      lines++
    }
    return lines
  }

  protected open inner class CommonTextBuilder : TextService.Builder<TCommandSource> {

    private var callback: Consumer<TCommandSource>? = null

    private var elements: Deque<Any> = LinkedList()
    private var hoverEvent: HoverEvent<*>? = null
    private var clickEvent: ClickEvent? = null

    override fun append(vararg contents: CharSequence): TextService.Builder<TCommandSource> {
      return append(*contents as Array<out Any>)
    }

    override fun appendCount(count: Int, vararg contents: Any): TextService.Builder<TCommandSource> {
      for (i in 0 until count) {
        append(*contents)
      }
      return this
    }

    override fun appendCount(count: Int, vararg contents: CharSequence): TextService.Builder<TCommandSource> {
      for (i in 0 until count) {
        append(*contents)
      }
      return this
    }

    private fun appendWithPadding(
      before: ((Int, Component) -> Unit)?,
      after: ((Int, Component) -> Unit)?,
      width: Int, padding: Any, vararg contents: Any
    ): TextService.Builder<TCommandSource> {
      require(width >= 1) { "Width must be at least 1" }
      val paddingText = of(padding)
      val paddingLength = length(paddingText)
      require(paddingLength >= 1) { "Padding length must be at least 1" }
      require(width >= paddingLength) { "Padding length must not be greater than width" }
      val contentsText = of(*contents)
      val contentsLength = length(contentsText)
      require(width >= contentsLength) { "Contents length must not be greater than width" }
      val missingSpace = (width - contentsLength) / paddingLength
      val add = missingSpace != 0
      if (add && before != null) {
        before(missingSpace, paddingText)
      }
      append(contentsText)
      if (add && after != null) {
        after(missingSpace, paddingText)
      }
      return this
    }

    override fun appendWithPaddingLeft(width: Int, padding: Any, vararg contents: Any): TextService.Builder<TCommandSource> {
      return appendWithPadding(this::appendCount, null, width, padding, *contents)
    }

    override fun appendWithPaddingLeft(
      width: Int,
      padding: Any,
      vararg contents: CharSequence
    ): TextService.Builder<TCommandSource> {
      return appendWithPaddingLeft(width, padding, contents)
    }

    override fun appendWithPaddingAround(width: Int, padding: Any, vararg contents: Any): TextService.Builder<TCommandSource> {
      val bothEnds = { m: Int, c: Component -> appendCount(m / 2, c); Unit }
      return appendWithPadding(bothEnds, bothEnds, width, padding, *contents)
    }

    override fun appendWithPaddingAround(
      width: Int,
      padding: Any,
      vararg contents: CharSequence
    ): TextService.Builder<TCommandSource> {
      return appendWithPaddingAround(width, padding, contents)
    }

    override fun appendWithPaddingRight(width: Int, padding: Any, vararg contents: Any): TextService.Builder<TCommandSource> {
      return appendWithPadding(null, this::appendCount, width, padding, *contents)
    }

    override fun appendWithPaddingRight(
      width: Int,
      padding: Any,
      vararg contents: CharSequence
    ): TextService.Builder<TCommandSource> {
      return appendWithPaddingRight(width, padding, contents)
    }

    override fun appendIf(
      condition: Boolean, vararg contents: Any
    ): TextService.Builder<TCommandSource> {
      return if (condition) append(*contents) else this
    }

    override fun appendIf(
      condition: Boolean, vararg contents: CharSequence
    ): TextService.Builder<TCommandSource> {
      return if (condition) append(*contents) else this
    }

    override fun appendJoining(
      delimiter: Any, vararg contents: CharSequence
    ): TextService.Builder<TCommandSource> {
      return appendJoining(delimiter, contents)
    }

    override fun appendJoiningIf(
      condition: Boolean, delimiter: Any, vararg contents: Any
    ): TextService.Builder<TCommandSource> {
      return if (condition) appendJoining(delimiter, contents) else this
    }

    override fun appendJoiningIf(
      condition: Boolean, delimiter: Any, vararg contents: CharSequence
    ): TextService.Builder<TCommandSource> {
      return if (condition) appendJoining(delimiter, contents) else this
    }

    override fun appendPrefix(): TextService.Builder<TCommandSource> {
      return append(pluginInfo.prefix)
    }

    override fun onHoverShowText(builder: TextService.Builder<TCommandSource>): TextService.Builder<TCommandSource> {
      return onHoverShowText(builder.build())
    }

    override fun onClickExecuteCallback(callback: Consumer<TCommandSource>): TextService.Builder<TCommandSource> {
      this.callback = callback
      return this
    }

    private fun initializeCallback() {
      val uuid = UUID.randomUUID()
      callbackCommand.addCallback(uuid, callback!!)
      val command: String = when (Anvil.getPlatform().name) {
        "bungee" -> "/anvilb:callback "
        "velocity" -> "/anvilv:callback "
        else -> "/anvil:callback "
      }
      onClickRunCommand(command + uuid)
    }

    override fun sendTo(receiver: TCommandSource) = send(build(), receiver)
    override fun sendToConsole() = sendToConsole(build())

    override fun aqua(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.AQUA)
      return this
    }

    override fun black(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.BLACK)
      return this
    }

    override fun blue(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.BLUE)
      return this
    }

    override fun dark_aqua(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.DARK_AQUA)
      return this
    }

    override fun dark_blue(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.DARK_BLUE)
      return this
    }

    override fun dark_gray(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.DARK_GRAY)
      return this
    }

    override fun dark_green(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.DARK_GREEN)
      return this
    }

    override fun dark_purple(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.DARK_PURPLE)
      return this
    }

    override fun dark_red(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.DARK_RED)
      return this
    }

    override fun gold(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.GOLD)
      return this
    }

    override fun gray(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.GRAY)
      return this
    }

    override fun green(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.GREEN)
      return this
    }

    override fun light_purple(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.LIGHT_PURPLE)
      return this
    }

    override fun red(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.RED)
      return this
    }

    override fun reset(): TextService.Builder<TCommandSource> = white()

    override fun white(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.WHITE)
      return this
    }

    override fun yellow(): TextService.Builder<TCommandSource> {
      elements.add(NamedTextColor.YELLOW)
      return this
    }

    override fun bold(): TextService.Builder<TCommandSource> {
      elements.add(TextDecoration.BOLD)
      return this
    }

    override fun italic(): TextService.Builder<TCommandSource> {
      elements.add(TextDecoration.ITALIC)
      return this
    }

    override fun obfuscated(): TextService.Builder<TCommandSource> {
      elements.add(TextDecoration.OBFUSCATED)
      return this
    }

    override fun strikethrough(): TextService.Builder<TCommandSource> {
      elements.add(TextDecoration.STRIKETHROUGH)
      return this
    }

    override fun underlined(): TextService.Builder<TCommandSource> {
      elements.add(TextDecoration.UNDERLINED)
      return this
    }

    override fun append(vararg contents: Any): TextService.Builder<TCommandSource> {
      for (o in contents) {
        when (o) {
          is TextService.Builder<*>, is Component, is TextColor -> elements.add(o)
          else -> elements.add(Component.text(o.toString()))
        }
      }
      return this
    }

    override fun appendJoining(delimiter: Any, vararg contents: Any): TextService.Builder<TCommandSource> {
      var delim = delimiter
      if (!(delimiter is TextService.Builder<*> || delimiter is Component)) {
        delim = Component.text(delimiter.toString())
      }
      val indexOfLast = contents.size - 1
      for (i in 0..indexOfLast) {
        when (val o = contents[i]) {
          is TextService.Builder<*>, is Component, is TextColor -> elements.add(o)
          else -> elements.add(Component.text(o.toString()))
        }
        if (i != indexOfLast) {
          elements.add(delim)
        }
      }
      return this
    }

    override fun onHoverShowText(text: Component): TextService.Builder<TCommandSource> {
      hoverEvent = HoverEvent.showText(text)
      return this
    }

    override fun onClickSuggestCommand(command: String): TextService.Builder<TCommandSource> {
      clickEvent = ClickEvent.suggestCommand(command)
      return this
    }

    override fun onClickRunCommand(command: String): TextService.Builder<TCommandSource> {
      clickEvent = ClickEvent.runCommand(command)
      return this
    }

    override fun onClickOpenUrl(url: URL): TextService.Builder<TCommandSource> {
      clickEvent = ClickEvent.openUrl(url)
      return this
    }

    override fun onClickOpenUrl(url: String): TextService.Builder<TCommandSource> {
      clickEvent = ClickEvent.openUrl(url)
      return this
    }

    override fun build(): Component {
      val hover = hoverEvent != null
      if (callback != null) {
        initializeCallback()
      }
      val click = clickEvent != null

      if (elements.isEmpty()) {
        return Component.empty()
      } else if (elements.size == 1 && !hover && !click) {
        when (val o = elements.first) {
          is TextService.Builder<*> -> return o.build()
          is Component -> return o
        }
      }

      // one builder for every color
      val components: Deque<Component> = LinkedList()

      // create first builder
      var currentBuilder = Component.text()
      val firstColor = elements.peekFirst()
      if (firstColor is TextColor) {
        currentBuilder.color(firstColor as TextColor)
        elements.pollFirst() // remove color because its already added to builder
      }

      for (o in elements) {
        when (o) {
          is TextService.Builder<*> -> currentBuilder.append(o.build())
          is Component -> currentBuilder.append(o)
          is TextColor -> {
            // build current builder
            components.offer(currentBuilder.build())
            // create new builder starting at this point until the next color
            currentBuilder = Component.text().color(o)
          }
          else -> logger.error("Skipping {} because it does not match any of the correct types.", o)
        }
      }

      // build last builder
      components.offer(currentBuilder.build())

      // create new builder with all previous components
      currentBuilder = Component.text().append(components)

      if (hover) {
        currentBuilder.hoverEvent(hoverEvent)
      }
      if (click) {
        currentBuilder.clickEvent(clickEvent)
      }
      return currentBuilder.build()
    }
  }

  inner class CommonExtendedPaginationBuilder : TextService.PaginationBuilder<TCommandSource> {

    var contents: MutableList<Component> = mutableListOf()
    var title: Component? = null
    var header: Component? = null
    var footer: Component? = null
    var padding: Component = Component.text("-").color(NamedTextColor.DARK_GREEN)
    var linesPerPage: Int = 20

    override fun contents(vararg contents: Component): TextService.PaginationBuilder<TCommandSource> {
      this.contents = mutableListOf(*contents)
      return this
    }

    override fun contents(contents: Iterable<Component>): TextService.PaginationBuilder<TCommandSource> {
      this.contents = ArrayList(64)
      contents.forEach(this.contents::add)
      return this
    }

    override fun title(title: Component?): TextService.PaginationBuilder<TCommandSource> {
      this.title = title
      return this
    }

    override fun title(title: TextService.Builder<TCommandSource>?): TextService.PaginationBuilder<TCommandSource> {
      return title(title?.build())
    }

    override fun header(header: Component?): TextService.PaginationBuilder<TCommandSource> {
      this.header = header
      return this
    }

    override fun header(header: TextService.Builder<TCommandSource>?): TextService.PaginationBuilder<TCommandSource> {
      return header(header?.build())
    }

    override fun footer(footer: Component?): TextService.PaginationBuilder<TCommandSource> {
      this.footer = footer
      return this
    }

    override fun footer(footer: TextService.Builder<TCommandSource>?): TextService.PaginationBuilder<TCommandSource> {
      return footer(footer?.build())
    }

    override fun padding(padding: Component): TextService.PaginationBuilder<TCommandSource> {
      this.padding = padding
      return this
    }

    override fun padding(padding: TextService.Builder<TCommandSource>): TextService.PaginationBuilder<TCommandSource> {
      return padding(padding.build())
    }

    override fun linesPerPage(linesPerPage: Int): TextService.PaginationBuilder<TCommandSource> {
      require(linesPerPage >= 1) { "Lines per page must be at least 1" }
      this.linesPerPage = linesPerPage
      return this
    }

    override fun build(): TextService.Pagination<TCommandSource> {
      return CommonExtendedPagination(contents, title, header, footer, padding, linesPerPage)
    }
  }

  protected inner class CommonExtendedPagination(
    var contents: List<Component>,
    var title: Component?,
    var header: Component?,
    var footer: Component?,
    private var padding0: Component,
    var linesPerPage0: Int
  ) : TextService.Pagination<TCommandSource> {

    var pages: MutableList<Component> = mutableListOf()

    override fun getContents(): Iterable<Component> = contents
    override fun getTitle(): Optional<Component> = Optional.ofNullable(title)
    override fun getHeader(): Optional<Component> = Optional.ofNullable(header)
    override fun getFooter(): Optional<Component> = Optional.ofNullable(footer)
    override fun getPadding(): Component = padding0
    override fun getLinesPerPage(): Int = linesPerPage0

    private fun buildPages() {
      val lineWidth: Int = when (platform.name) {
        "sponge" -> 91
        else -> 51
      }
      pages = mutableListOf()
      var contentsIndex = 0 // index in contents list
      val contentsSize = contents.size
      var isFirstPage = true
      outer@ while (contentsIndex < contentsSize) {
        val page = builder()
        var linesAvailable = linesPerPage - 1
        if (title != null) {
          page.appendWithPaddingAround(lineWidth, ' ', title).append("\n")
          linesAvailable -= lineCount(title)
        }
        if (header != null) {
          page.appendWithPaddingAround(lineWidth, ' ', header).append("\n")
          linesAvailable -= lineCount(header)
        } else {
          page.appendWithPaddingAround(lineWidth, padding, "").append("\n")
        }
        var withFooter = false
        if (footer != null) {
          // reserve space for footer, will be added later
          withFooter = true
          linesAvailable -= lineCount(footer)
        }
        while (linesAvailable > 0) {
          // check if there are any contents left
          if (contentsIndex >= contentsSize) {
            // dont add empty lines on first page
            if (isFirstPage) {
              break
            }
            // no more content, add an empty line
            page.append("\n")
          }
          val next = contents[contentsIndex]
          // make sure there's enough space
          if (linesAvailable < lineCount(next)) {
            continue@outer
          }
          page.append(next, "\n")
          ++contentsIndex
          --linesAvailable
        }
        if (isFirstPage) {
          isFirstPage = false
        }
        if (withFooter) {
          page.appendWithPaddingAround(lineWidth, padding, footer)
        } else {
          page.appendWithPaddingAround(lineWidth, padding, "").append("\n")
        }
        pages.add(page.build())
      }
    }

    override fun sendTo(receiver: TCommandSource) {
      if (pages.isEmpty()) {
        buildPages()
      }
      send(pages[0], receiver)
    }

    override fun sendToConsole() = sendTo(console)
  }
}
