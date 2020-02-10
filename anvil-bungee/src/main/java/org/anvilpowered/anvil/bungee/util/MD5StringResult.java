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

package org.anvilpowered.anvil.bungee.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.common.util.CommonStringResult;

import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;

public abstract class MD5StringResult<TCommandSource> extends CommonStringResult<TextComponent, TCommandSource> {

    @Override
    public Builder<TextComponent, TCommandSource> builder() {
        return new MD5StringResultBuilder();
    }

    @Override
    public TextComponent deserialize(String text) {
        return new TextComponent(TextComponent.fromLegacyText(text));
    }

    @Override
    public String serialize(TextComponent text) {
        return text.toLegacyText();
    }

    protected class MD5StringResultBuilder extends CommonStringResultBuilder {
        private final Deque<Object> elements;
        private HoverEvent hoverEvent;
        private ClickEvent clickEvent;

        protected MD5StringResultBuilder() {
            this.elements = new LinkedList<>();
        }

        @Override
        public Builder<TextComponent, TCommandSource> aqua() {
            elements.add(ChatColor.AQUA);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> black() {
            elements.add(ChatColor.BLACK);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> blue() {
            elements.add(ChatColor.BLUE);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> dark_aqua() {
            elements.add(ChatColor.DARK_AQUA);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> dark_blue() {
            elements.add(ChatColor.DARK_BLUE);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> dark_gray() {
            elements.add(ChatColor.DARK_GRAY);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> dark_green() {
            elements.add(ChatColor.DARK_GREEN);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> dark_purple() {
            elements.add(ChatColor.DARK_PURPLE);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> dark_red() {
            elements.add(ChatColor.DARK_RED);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> gold() {
            elements.add(ChatColor.GOLD);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> gray() {
            elements.add(ChatColor.GRAY);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> green() {
            elements.add(ChatColor.GREEN);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> light_purple() {
            elements.add(ChatColor.LIGHT_PURPLE);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> red() {
            elements.add(ChatColor.RED);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> reset() {
            elements.add(ChatColor.RESET);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> white() {
            elements.add(ChatColor.WHITE);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> yellow() {
            elements.add(ChatColor.YELLOW);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> append(Object... content) {
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
        public Builder<TextComponent, TCommandSource> appendJoining(Object delimiter, Object... content) {
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
        public Builder<TextComponent, TCommandSource> onHoverShowText(TextComponent content) {
            hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(content).create());
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> onHoverShowText(Builder<TextComponent, TCommandSource> builder) {
            return onHoverShowText(builder.build());
        }

        @Override
        public Builder<TextComponent, TCommandSource> onClickSuggestCommand(String command) {
            clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> onClickRunCommand(String command) {
            clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> onClickExecuteCallback(Consumer<TCommandSource> callback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder<TextComponent, TCommandSource> onClickOpenUrl(String url) {
            clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            return this;
        }

        @Override
        public Builder<TextComponent, TCommandSource> onClickOpenUrl(URL url) {
            return onClickOpenUrl(url.toString());
        }

        @Override
        @SuppressWarnings("unchecked")
        public TextComponent build() {

            if (elements.isEmpty()) {
                return new TextComponent();
            }

            // one builder for every color
            final Deque<BaseComponent> components = new LinkedList<>();

            // create first builder
            TextComponent currentBuilder = new TextComponent();
            Object firstColor = elements.peekFirst();
            if (firstColor instanceof ChatColor) {
                currentBuilder.setColor((ChatColor) firstColor);
                elements.pollFirst(); // remove color because its already added to builder
            }

            for (Object o : elements) {
                if (o instanceof Builder) {
                    currentBuilder.addExtra(((Builder<TextComponent, TCommandSource>) o).build());
                } else if (o instanceof BaseComponent) {
                    currentBuilder.addExtra((BaseComponent) o);
                } else if (o instanceof ChatColor) {
                    // build current builder
                    components.offer(currentBuilder);
                    // create new builder starting at this point until the next color
                    currentBuilder = new TextComponent();
                    currentBuilder.setColor((ChatColor) o);
                } else {
                    System.err.println("Skipping " + o + " because it does not match any of the correct types");
                }
            }

            // build last builder
            components.offer(currentBuilder);

            // create new builder with all previous components
            currentBuilder = new TextComponent(components.toArray(new BaseComponent[0]));

            if (hoverEvent != null) {
                currentBuilder.setHoverEvent(hoverEvent);
            }
            if (clickEvent != null) {
                currentBuilder.setClickEvent(clickEvent);
            }
            return currentBuilder;
        }
    }
}
