/*
 * Anvil - AnvilPowered
 *   Copyright (C) 2020
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package org.anvilpowered.anvil.sponge7.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.anvilpowered.anvil.common.util.CommonTextService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers

open class Sponge7TextService : CommonTextService<CommandSource>() {

  override fun send(text: Component, receiver: CommandSource) {
    receiver.sendMessage(
      Text.of(
        TextSerializers.FORMATTING_CODE.deserialize(LegacyComponentSerializer.legacyAmpersand().serialize(text))
      )
    )
  }

  override fun getConsole(): CommandSource = Sponge.getServer().console
  /*override fun builder(): TextService.Builder<Text, CommandSource> {
    return SpongeTextBuilder()
  }

  override fun paginationBuilder(): TextService.PaginationBuilder<Text, CommandSource> = SpongePaginationBuilder()
  override fun send(text: Text, receiver: CommandSource) = receiver.sendMessage(text)
  override fun getConsole(): CommandSource = Sponge.getServer().console
  override fun deserialize(text: String): Text = TextSerializers.FORMATTING_CODE.deserialize(text)
  override fun serialize(text: Text): String = TextSerializers.FORMATTING_CODE.serialize(text)
  override fun serializePlain(text: Text): String = text.toPlain()

  protected inner class SpongeTextBuilder : CommonTextBuilder() {
    private val elements: MutableList<TextElement>
    private lateinit var hoverAction: HoverAction<*>
    private lateinit var clickAction: ClickAction<*>
    override fun aqua(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.AQUA)
      return this
    }

    override fun black(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.BLACK)
      return this
    }

    override fun blue(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.BLUE)
      return this
    }

    override fun dark_aqua(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.DARK_AQUA)
      return this
    }

    override fun dark_blue(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.DARK_BLUE)
      return this
    }

    override fun dark_gray(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.DARK_GRAY)
      return this
    }

    override fun dark_green(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.DARK_GREEN)
      return this
    }

    override fun dark_purple(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.DARK_PURPLE)
      return this
    }

    override fun dark_red(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.DARK_RED)
      return this
    }

    override fun gold(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.GOLD)
      return this
    }

    override fun gray(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.GRAY)
      return this
    }

    override fun green(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.GREEN)
      return this
    }

    override fun light_purple(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.LIGHT_PURPLE)
      return this
    }

    override fun red(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.RED)
      return this
    }

    override fun reset(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.RESET)
      return this
    }

    override fun white(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.WHITE)
      return this
    }

    override fun yellow(): TextService.Builder<Text, CommandSource> {
      elements.add(TextColors.YELLOW)
      return this
    }

    override fun bold(): TextService.Builder<Text, CommandSource> {
      elements.add(TextStyles.BOLD)
      return this
    }

    override fun italic(): TextService.Builder<Text, CommandSource> {
      elements.add(TextStyles.ITALIC)
      return this
    }

    override fun obfuscated(): TextService.Builder<Text, CommandSource> {
      elements.add(TextStyles.OBFUSCATED)
      return this
    }

    override fun strikethrough(): TextService.Builder<Text, CommandSource> {
      elements.add(TextStyles.STRIKETHROUGH)
      return this
    }

    override fun underlined(): TextService.Builder<Text, CommandSource> {
      elements.add(TextStyles.UNDERLINE)
      return this
    }

    override fun append(vararg contents: Any): TextService.Builder<Text, CommandSource> {
      for (o in contents) {
        if (o is TextService.Builder<*, *>) {
          elements.add((o as TextService.Builder<Text, CommandSource>).build())
        } else if (o is TextElement) {
          elements.add(o)
        } else {
          elements.add(Text.of(o))
        }
      }
      return this
    }

    override fun appendJoining(delimiter: Any, vararg contents: Any): TextService.Builder<Text, CommandSource> {
      val indexOfLast = contents.size - 1
      for (i in 0..indexOfLast) {
        val o = contents[i]
        if (o is TextService.Builder<*, *>) {
          elements.add((o as TextService.Builder<Text, CommandSource>).build())
        } else if (o is TextElement) {
          elements.add(o)
        } else {
          elements.add(Text.of(o))
        }
        if (i != indexOfLast) {
          elements.add(Text.of(delimiter))
        }
      }
      return this
    }

    override fun onHoverShowText(content: Text): TextService.Builder<Text, CommandSource> {
      hoverAction = TextActions.showText(content)
      return this
    }

    override fun onClickSuggestCommand(command: String): TextService.Builder<Text, CommandSource> {
      clickAction = TextActions.suggestCommand(command)
      return this
    }

    override fun onClickRunCommand(command: String): TextService.Builder<Text, CommandSource> {
      clickAction = TextActions.runCommand(command)
      return this
    }

    override fun onClickExecuteCallback(
      callback: Consumer<CommandSource>
    ): TextService.Builder<Text, CommandSource> {
      clickAction = TextActions.executeCallback(callback)
      return this
    }

    override fun onClickOpenUrl(url: URL): TextService.Builder<Text, CommandSource> {
      clickAction = TextActions.openUrl(url)
      return this
    }

    override fun onClickOpenUrl(url: String): TextService.Builder<Text, CommandSource> {
      try {
        clickAction = TextActions.openUrl(URL(url))
      } catch (e: MalformedURLException) {
        e.printStackTrace()
      }
      return this
    }

    override fun build(): Text {
      val hover = hoverAction != null
      val click = clickAction != null
      if (elements.size == 1 && !hover && !click) {
        val o = elements[0]
        return if (o is TextService.Builder<*, *>) {
          (o as TextService.Builder<Text, CommandSource>).build()
        } else Text.of(o)
      }
      val builder = Text.builder().append(Text.of(*elements.toTypedArray()))
      if (hover) {
        builder.onHover(hoverAction)
      }
      if (click) {
        builder.onClick(clickAction)
      }
      return builder.build()
    }

    init {
      elements = ArrayList()
    }
  }

  protected inner class SpongePaginationBuilder : CommonPaginationBuilder() {
    private val builder: PaginationList.Builder =
      Sponge.getServiceManager().provideUnchecked(PaginationService::class.java).builder()

    override fun contents(
      vararg contents: Text
    ): TextService.PaginationBuilder<Text, CommandSource> {
      builder.contents(*contents)
      return this
    }

    override fun contents(
      contents: Iterable<Text>
    ): TextService.PaginationBuilder<Text, CommandSource> {
      builder.contents(contents)
      return this
    }

    override fun title(
      title: Text?
    ): TextService.PaginationBuilder<Text, CommandSource> {
      builder.title(title)
      return this
    }

    override fun header(
      header: Text?
    ): TextService.PaginationBuilder<Text, CommandSource> {
      builder.header(header)
      return this
    }

    override fun footer(
      footer: Text?
    ): TextService.PaginationBuilder<Text, CommandSource> {
      builder.footer(footer)
      return this
    }

    override fun padding(
      padding: Text
    ): TextService.PaginationBuilder<Text, CommandSource> {
      builder.padding(padding)
      return this
    }

    override fun linesPerPage(
      linesPerPage: Int
    ): TextService.PaginationBuilder<Text, CommandSource> {
      require(linesPerPage >= 1) { "Lines per page must be at least 1" }
      builder.linesPerPage(linesPerPage)
      return this
    }

    override fun build(): TextService.Pagination<Text, CommandSource> = SpongePagination(builder.build())
  }

  protected class SpongePagination(private val paginationList: PaginationList) :
    TextService.Pagination<Text, CommandSource> {

    override fun getContents(): Iterable<Text> = paginationList.contents
    override fun getTitle(): Optional<Text> = paginationList.title
    override fun getHeader(): Optional<Text> = paginationList.header
    override fun getFooter(): Optional<Text> = paginationList.footer
    override fun getPadding(): Text = paginationList.padding
    override fun getLinesPerPage(): Int = paginationList.linesPerPage
    override fun sendTo(receiver: CommandSource) = paginationList.sendTo(receiver)
    override fun sendToConsole() = sendTo(Sponge.getServer().console)
  }*/
}
