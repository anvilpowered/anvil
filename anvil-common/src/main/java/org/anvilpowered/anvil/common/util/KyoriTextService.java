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

package org.anvilpowered.anvil.common.util;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;

public abstract class KyoriTextService<TCommandSource> extends CommonTextService<TCommandSource> {

    @Inject
    private Logger logger;

    @Override
    public Builder<TCommandSource> builder() {
        return new VelocityTextBuilder();
    }

    @Override
    public Component deserialize(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    @Override
    public String serialize(Component text) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(text);
    }

    @Override
    public String serializePlain(Component text) {
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
        public Builder<TCommandSource> aqua() {
            elements.add(NamedTextColor.AQUA);
            return this;
        }

        @Override
        public Builder<TCommandSource> black() {
            elements.add(NamedTextColor.BLACK);
            return this;
        }

        @Override
        public Builder<TCommandSource> blue() {
            elements.add(NamedTextColor.BLUE);
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_aqua() {
            elements.add(NamedTextColor.DARK_AQUA);
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_blue() {
            elements.add(NamedTextColor.DARK_BLUE);
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_gray() {
            elements.add(NamedTextColor.DARK_GRAY);
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_green() {
            elements.add(NamedTextColor.DARK_GREEN);
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_purple() {
            elements.add(NamedTextColor.DARK_PURPLE);
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_red() {
            elements.add(NamedTextColor.DARK_RED);
            return this;
        }

        @Override
        public Builder<TCommandSource> gold() {
            elements.add(NamedTextColor.GOLD);
            return this;
        }

        @Override
        public Builder<TCommandSource> gray() {
            elements.add(NamedTextColor.GRAY);
            return this;
        }

        @Override
        public Builder<TCommandSource> green() {
            elements.add(NamedTextColor.GREEN);
            return this;
        }

        @Override
        public Builder<TCommandSource> light_purple() {
            elements.add(NamedTextColor.LIGHT_PURPLE);
            return this;
        }

        @Override
        public Builder<TCommandSource> red() {
            elements.add(NamedTextColor.RED);
            return this;
        }

        @Override
        public Builder<TCommandSource> reset() {
            elements.add(NamedTextColor.WHITE);
            return this;
        }

        @Override
        public Builder<TCommandSource> white() {
            elements.add(NamedTextColor.WHITE);
            return this;
        }

        @Override
        public Builder<TCommandSource> yellow() {
            elements.add(NamedTextColor.YELLOW);
            return this;
        }

        @Override
        public Builder<TCommandSource> bold() {
            elements.add(TextDecoration.BOLD);
            return this;
        }

        @Override
        public Builder<TCommandSource> italic() {
            elements.add(TextDecoration.ITALIC);
            return this;
        }

        @Override
        public Builder<TCommandSource> obfuscated() {
            elements.add(TextDecoration.OBFUSCATED);
            return this;
        }

        @Override
        public Builder<TCommandSource> strikethrough() {
            elements.add(TextDecoration.STRIKETHROUGH);
            return this;
        }

        @Override
        public Builder<TCommandSource> underlined() {
            elements.add(TextDecoration.UNDERLINED);
            return this;
        }

        @Override
        public Builder<TCommandSource> append(Object... contents) {
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
        public Builder<TCommandSource> appendJoining(
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
        public Builder<TCommandSource> onHoverShowText(Component text) {
            hoverEvent = HoverEvent.showText(text);
            return this;
        }

        @Override
        public Builder<TCommandSource> onClickSuggestCommand(String command) {
            callback = null;
            clickEvent = ClickEvent.suggestCommand(command);
            return this;
        }

        @Override
        public Builder<TCommandSource> onClickRunCommand(String command) {
            callback = null;
            clickEvent = ClickEvent.runCommand(command);
            return this;
        }

        @Override
        public Builder<TCommandSource> onClickOpenUrl(URL url) {
            return onClickOpenUrl(url.toString());
        }

        @Override
        public Builder<TCommandSource> onClickOpenUrl(String url) {
            callback = null;
            clickEvent = ClickEvent.openUrl(url);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Component build() {
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
                    return ((Builder<TCommandSource>) o).build();
                } else if (o instanceof Component) {
                    return (Component) o;
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
                    currentBuilder.append(((Builder<TCommandSource>) o).build());
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
