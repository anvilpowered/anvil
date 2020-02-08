/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.spigot.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.common.util.CommonStringResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpigotStringResult extends CommonStringResult<TextComponent, CommandSender> {
    @Override
    public Builder<TextComponent, CommandSender> builder() {
        return new SpigotStringResultBuilder();
    }

    @Override
    public void send(TextComponent result, CommandSender commandSender) {

    }

    @Override
    public TextComponent deserialize(String text) {
        return null;
    }

    @Override
    public String serialize(TextComponent text) {
        return null;
    }

    private static final class SpigotStringResultBuilder extends CommonStringResultBuilder<TextComponent, CommandSender> {
        private final List<Object> elements;
        private HoverEvent hoverEvent;
        private ClickEvent clickEvent;

        private SpigotStringResultBuilder() {
            this.elements = new ArrayList<>();
        }

        @Override
        public Builder<TextComponent, CommandSender> aqua() {
            elements.add(ChatColor.AQUA);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> black() {
            elements.add(ChatColor.BLACK);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> blue() {
            elements.add(ChatColor.BLUE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> dark_aqua() {
            elements.add(ChatColor.DARK_AQUA);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> dark_blue() {
            elements.add(ChatColor.DARK_BLUE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> dark_gray() {
            elements.add(ChatColor.DARK_GRAY);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> dark_green() {
            elements.add(ChatColor.DARK_GREEN);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> dark_purple() {
            elements.add(ChatColor.DARK_PURPLE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> dark_red() {
            elements.add(ChatColor.DARK_RED);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> gold() {
            elements.add(ChatColor.GOLD);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> gray() {
            elements.add(ChatColor.GRAY);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> green() {
            elements.add(ChatColor.GREEN);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> light_purple() {
            elements.add(ChatColor.LIGHT_PURPLE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> red() {
            elements.add(ChatColor.RED);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> reset() {
            elements.add(ChatColor.RESET);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> white() {
            elements.add(ChatColor.WHITE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> yellow() {
            elements.add(ChatColor.YELLOW);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> append(Object... content) {
            for (Object o : content) {
                if (o instanceof Builder || o instanceof TextComponent || o instanceof ChatColor) {
                    elements.add(o);
                } else {
                    elements.add(o.toString());
                }
            }
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> appendJoining(Object delimiter, Object... content) {
            final int indexOfLast = content.length - 1;
            for (int i = 0; i <= indexOfLast; i++) {
                Object o = content[i];
                if (o instanceof Builder || o instanceof TextComponent || o instanceof ChatColor) {
                    elements.add(o);
                } else {
                    elements.add(o.toString());
                }
                if (i != indexOfLast) {
                    if (delimiter instanceof Builder || delimiter instanceof TextComponent) {
                        elements.add(delimiter);
                    } else {
                        elements.add(delimiter.toString());
                    }
                }
            }
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> onHoverShowText(TextComponent content) {
            hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(content).create());
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> onHoverShowText(Builder<TextComponent, CommandSender> builder) {
            return onHoverShowText(builder.build());
        }

        @Override
        public Builder<TextComponent, CommandSender> onClickSuggestCommand(String command) {
            clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> onClickRunCommand(String command) {
            clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> onClickExecuteCallback(Consumer<CommandSender> callback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder<TextComponent, CommandSender> onClickOpenUrl(String url) {
            clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSender> onClickOpenUrl(URL url) {
            return onClickOpenUrl(url.toString());
        }

        @Override
        public TextComponent build() {
            TextComponent textComponent = new TextComponent();
            if (hoverEvent != null) {
                textComponent.setHoverEvent(hoverEvent);
            }
            if (clickEvent != null) {
                textComponent.setClickEvent(clickEvent);
            }
            return textComponent;
        }

        @Override
        public void sendTo(CommandSender commandSender) {
            commandSender.sendMessage(build().getText());
        }
    }
}
