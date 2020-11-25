/*
 *   Anvil - AnvilPowered
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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.velocity.util;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.anvilpowered.anvil.common.util.CommonTextService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

public class VelocityTextService extends CommonTextService<TextComponent, CommandSource> {

    @Inject
    private Logger logger;

    @Inject
    protected ProxyServer proxyServer;

    @Override
    public Builder<TextComponent, CommandSource> builder() {
        return new VelocityTextBuilder();
    }

    @Override
    public void send(TextComponent text, CommandSource receiver) {
        receiver.sendMessage(Identity.nil(), text);
    }

    @Override
    public void send(TextComponent text, CommandSource receiver, UUID sourceUUID) {
        receiver.sendMessage(Identity.identity(sourceUUID), text);
    }

    @Override
    public void send(TextComponent text, CommandSource receiver, Object source) {
        if (source instanceof Identified) {
            receiver.sendMessage((Identified) source, text);
        } else {
            send(text, receiver);
        }
    }

    @Override
    public CommandSource getConsole() {
        return proxyServer.getConsoleCommandSource();
    }

    @Override
    public TextComponent deserialize(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    @Override
    public String serialize(TextComponent text) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(text);
    }

    @Override
    public String serializePlain(TextComponent text) {
        return PlainComponentSerializer.plain().serialize(text.asComponent());
    }

    protected class VelocityTextBuilder extends CommonTextBuilder {

        private final Deque<Object> elements;
        @Nullable
        private HoverEvent<?> hoverEvent;
        @Nullable
        private ClickEvent clickEvent;

        protected VelocityTextBuilder() {
            elements = new LinkedList<>();
        }

        @Override
        public Builder<TextComponent, CommandSource> aqua() {
            elements.add(NamedTextColor.AQUA);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> black() {
            elements.add(NamedTextColor.BLACK);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> blue() {
            elements.add(NamedTextColor.BLUE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_aqua() {
            elements.add(NamedTextColor.DARK_AQUA);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_blue() {
            elements.add(NamedTextColor.DARK_BLUE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_gray() {
            elements.add(NamedTextColor.DARK_GRAY);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_green() {
            elements.add(NamedTextColor.DARK_GREEN);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_purple() {
            elements.add(NamedTextColor.DARK_PURPLE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_red() {
            elements.add(NamedTextColor.DARK_RED);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> gold() {
            elements.add(NamedTextColor.GOLD);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> gray() {
            elements.add(NamedTextColor.GRAY);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> green() {
            elements.add(NamedTextColor.GREEN);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> light_purple() {
            elements.add(NamedTextColor.LIGHT_PURPLE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> red() {
            elements.add(NamedTextColor.RED);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> reset() {
            elements.add(NamedTextColor.WHITE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> white() {
            elements.add(NamedTextColor.WHITE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> yellow() {
            elements.add(NamedTextColor.YELLOW);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> bold() {
            elements.add(TextDecoration.BOLD);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> italic() {
            elements.add(TextDecoration.ITALIC);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> obfuscated() {
            elements.add(TextDecoration.OBFUSCATED);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> strikethrough() {
            elements.add(TextDecoration.STRIKETHROUGH);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> underlined() {
            elements.add(TextDecoration.UNDERLINED);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> append(Object... contents) {
            for (Object o : contents) {
                if (o instanceof Builder || o instanceof Component || o instanceof TextColor) {
                    elements.add(o);
                } else {
                    elements.add(Component.text(String.valueOf(o)));
                }
            }
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> appendJoining(
            Object delimiter, Object... contents) {
            if (!(delimiter instanceof Builder || delimiter instanceof Component)) {
                delimiter = Component.text(delimiter.toString());
            }
            final int indexOfLast = contents.length - 1;
            for (int i = 0; i <= indexOfLast; i++) {
                Object o = contents[i];
                if (o instanceof Builder || o instanceof Component || o instanceof TextColor) {
                    elements.add(o);
                } else {
                    elements.add(Component.text(String.valueOf(o)));
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
            callback = null;
            clickEvent = ClickEvent.suggestCommand(command);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> onClickRunCommand(String command) {
            callback = null;
            clickEvent = ClickEvent.runCommand(command);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> onClickOpenUrl(URL url) {
            return onClickOpenUrl(url.toString());
        }

        @Override
        public Builder<TextComponent, CommandSource> onClickOpenUrl(String url) {
            callback = null;
            clickEvent = ClickEvent.openUrl(url);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public TextComponent build() {
            boolean hover = hoverEvent != null;
            if (callback != null) {
                initializeCallback();
            }
            boolean click = clickEvent != null;

            if (elements.isEmpty()) {
                return Component.empty();
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
            TextComponent.Builder currentBuilder = Component.text();
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
                    currentBuilder = Component.text().color((TextColor) o);
                } else {
                    logger.error("Skipping {} because it does not match any of the correct types.", o);
                }
            }

            // build last builder
            components.offer(currentBuilder.build());

            // create new builder with all previous components
            currentBuilder = Component.text().append(components);

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
