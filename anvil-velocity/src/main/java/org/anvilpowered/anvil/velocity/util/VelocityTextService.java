/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.velocity.util;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.text.serializer.plain.PlainComponentSerializer;
import org.anvilpowered.anvil.common.util.CommonTextService;

import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;

public class VelocityTextService extends CommonTextService<TextComponent, CommandSource> {

    @Inject
    protected ProxyServer proxyServer;

    @Override
    public Builder<TextComponent, CommandSource> builder() {
        return new VelocityTextBuilder();
    }

    @Override
    public void send(TextComponent text, CommandSource commandSource) {
        commandSource.sendMessage(text);
    }

    @Override
    public CommandSource getConsole() {
        return proxyServer.getConsoleCommandSource();
    }

    @Override
    public TextComponent deserialize(String text) {
        return LegacyComponentSerializer.legacy().deserialize(text, '&');
    }

    @Override
    public String serialize(TextComponent text) {
        return LegacyComponentSerializer.legacy().serialize(text, '&');
    }

    @Override
    public String serializePlain(TextComponent text) {
        return PlainComponentSerializer.INSTANCE.serialize(text);
    }

    protected class VelocityTextBuilder extends CommonTextBuilder {

        private final Deque<Object> elements;
        private HoverEvent hoverEvent;
        private ClickEvent clickEvent;

        protected VelocityTextBuilder() {
            elements = new LinkedList<>();
        }

        @Override
        public Builder<TextComponent, CommandSource> aqua() {
            elements.add(TextColor.AQUA);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> black() {
            elements.add(TextColor.BLACK);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> blue() {
            elements.add(TextColor.BLUE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_aqua() {
            elements.add(TextColor.DARK_AQUA);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_blue() {
            elements.add(TextColor.DARK_BLUE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_gray() {
            elements.add(TextColor.DARK_GRAY);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_green() {
            elements.add(TextColor.DARK_GREEN);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_purple() {
            elements.add(TextColor.DARK_PURPLE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_red() {
            elements.add(TextColor.DARK_RED);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> gold() {
            elements.add(TextColor.GOLD);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> gray() {
            elements.add(TextColor.GRAY);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> green() {
            elements.add(TextColor.GREEN);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> light_purple() {
            elements.add(TextColor.LIGHT_PURPLE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> red() {
            elements.add(TextColor.RED);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> reset() {
            elements.add(TextColor.WHITE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> white() {
            elements.add(TextColor.WHITE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> yellow() {
            elements.add(TextColor.YELLOW);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> append(Object... contents) {
            for (Object o : contents) {
                if (o instanceof Builder || o instanceof Component || o instanceof TextColor) {
                    elements.add(o);
                } else {
                    elements.add(TextComponent.of(String.valueOf(o)));
                }
            }
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> appendJoining(
            Object delimiter, Object... contents) {
            if (!(delimiter instanceof Builder || delimiter instanceof Component)) {
                delimiter = TextComponent.of(delimiter.toString());
            }
            final int indexOfLast = contents.length - 1;
            for (int i = 0; i <= indexOfLast; i++) {
                Object o = contents[i];
                if (o instanceof Builder || o instanceof Component || o instanceof TextColor) {
                    elements.add(o);
                } else {
                    elements.add(TextComponent.of(String.valueOf(o)));
                }
                if (i != indexOfLast) {
                    elements.add(delimiter);
                }
            }
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> onHoverShowText(TextComponent text) {
            hoverEvent = HoverEvent.showText(text);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> onClickSuggestCommand(String command) {
            clickEvent = ClickEvent.suggestCommand(command);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> onClickRunCommand(String command) {
            clickEvent = ClickEvent.runCommand(command);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> onClickExecuteCallback(
            Consumer<CommandSource> callback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder<TextComponent, CommandSource> onClickOpenUrl(URL url) {
            return onClickOpenUrl(url.toString());
        }

        @Override
        public Builder<TextComponent, CommandSource> onClickOpenUrl(String url) {
            clickEvent = ClickEvent.openUrl(url);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public TextComponent build() {
            boolean hover = hoverEvent != null;
            boolean click = clickEvent != null;

            if (elements.isEmpty()) {
                return TextComponent.empty();
            } else if (elements.size() == 1 && !hover && !click) {
                Object o = elements.getFirst();
                if (o instanceof Builder) {
                    return ((Builder<TextComponent, CommandSource>) o).build();
                } else if (o instanceof TextComponent) {
                    return (TextComponent) o;
                }
            }

            // one builder for every color
            final Deque<Component> components = new LinkedList<>();

            // create first builder
            TextComponent.Builder currentBuilder = TextComponent.builder();
            Object firstColor = elements.peekFirst();
            if (firstColor instanceof TextColor) {
                currentBuilder.color((TextColor) firstColor);
                elements.pollFirst(); // remove color because its already added to builder
            }

            for (Object o : elements) {
                if (o instanceof Builder) {
                    currentBuilder.append(((Builder<TextComponent, CommandSource>) o).build());
                } else if (o instanceof Component) {
                    currentBuilder.append((Component) o);
                } else if (o instanceof TextColor) {
                    // build current builder
                    components.offer(currentBuilder.build());
                    // create new builder starting at this point until the next color
                    currentBuilder = TextComponent.builder().color((TextColor) o);
                } else {
                    System.err.println("Skipping " + o
                        + " because it does not match any of the correct types");
                }
            }

            // build last builder
            components.offer(currentBuilder.build());

            // create new builder with all previous components
            currentBuilder = TextComponent.builder().append(components);

            if (hover) {
                currentBuilder.hoverEvent(hoverEvent);
            }
            if (click) {
                currentBuilder.clickEvent(clickEvent);
            }
            return currentBuilder.build();
        }
    }
}
