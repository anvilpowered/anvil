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

import net.kyori.adventure.text.Component;

import java.net.URL;
import java.util.function.Consumer;

public abstract class StringTextService<TCommandSource>
    extends CommonTextService<TCommandSource> {

    @Override
    public Component deserialize(String text) {
        return Component.text(text);
    }

    protected class StringTextBuilder extends CommonTextBuilder {
        protected final StringBuilder elements;

        public StringTextBuilder() {
            elements = new StringBuilder();
        }

        @Override
        public Builder<TCommandSource> aqua() {
            elements.append("\u00a7b");
            return this;
        }

        @Override
        public Builder<TCommandSource> black() {
            elements.append("\u00a70");
            return this;
        }

        @Override
        public Builder<TCommandSource> blue() {
            elements.append("\u00a79");
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_aqua() {
            elements.append("\u00a73");
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_blue() {
            elements.append("\u00a71");
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_gray() {
            elements.append("\u00a78");
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_green() {
            elements.append("\u00a72");
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_purple() {
            elements.append("\u00a75");
            return this;
        }

        @Override
        public Builder<TCommandSource> dark_red() {
            elements.append("\u00a74");
            return this;
        }

        @Override
        public Builder<TCommandSource> gold() {
            elements.append("\u00a76");
            return this;
        }

        @Override
        public Builder<TCommandSource> gray() {
            elements.append("\u00a77");
            return this;
        }

        @Override
        public Builder<TCommandSource> green() {
            elements.append("\u00a7a");
            return this;
        }

        @Override
        public Builder<TCommandSource> light_purple() {
            elements.append("\u00a7d");
            return this;
        }

        @Override
        public Builder<TCommandSource> red() {
            elements.append("\u00a7c");
            return this;
        }

        @Override
        public Builder<TCommandSource> reset() {
            elements.append("\u00a7r");
            return this;
        }

        @Override
        public Builder<TCommandSource> white() {
            elements.append("\u00a7f");
            return this;
        }

        @Override
        public Builder<TCommandSource> yellow() {
            elements.append("\u00a7e");
            return this;
        }

        @Override
        public Builder<TCommandSource> bold() {
            elements.append("\u00a7l");
            return this;
        }

        @Override
        public Builder<TCommandSource> italic() {
            elements.append("\u00a7o");
            return this;
        }

        @Override
        public Builder<TCommandSource> obfuscated() {
            elements.append("\u00a7k");
            return this;
        }

        @Override
        public Builder<TCommandSource> strikethrough() {
            elements.append("\u00a7m");
            return this;
        }

        @Override
        public Builder<TCommandSource> underlined() {
            elements.append("\u00a7n");
            return this;
        }

        @Override
        public Builder<TCommandSource> append(Object... contents) {
            for (Object o : contents) {
                if (o instanceof Builder) {
                    elements.append(((Builder) o).build());
                } else {
                    elements.append(o);
                }
            }
            return this;
        }

        @Override
        public Builder<TCommandSource> appendJoining(Object delimiter, Object... contents) {
            if (delimiter instanceof Builder) {
                delimiter = ((Builder) delimiter).build();
            }
            final int indexOfLast = contents.length - 1;
            for (int i = 0; i <= indexOfLast; i++) {
                Object o = contents[i];
                if (o instanceof Builder) {
                    elements.append(((Builder) o).build());
                } else {
                    elements.append(o);
                }
                if (i != indexOfLast) {
                    elements.append(delimiter);
                }
            }
            return this;
        }

        @Override
        public Builder<TCommandSource> onHoverShowText(Component text) {
            return this;
        }

        @Override
        public Builder<TCommandSource> onHoverShowText(Builder<TCommandSource> builder) {
            return this;
        }

        @Override
        public Builder<TCommandSource> onClickSuggestCommand(String command) {
            return this;
        }

        @Override
        public Builder<TCommandSource> onClickRunCommand(String command) {
            return this;
        }

        @Override
        public Builder<TCommandSource> onClickExecuteCallback(Consumer<TCommandSource> callback) {
            return this;
        }

        @Override
        public Builder<TCommandSource> onClickOpenUrl(URL url) {
            return this;
        }

        @Override
        public Builder<TCommandSource> onClickOpenUrl(String url) {
            return this;
        }

        @Override
        public Component build() {
            return Component.text(elements.toString());
        }
    }
}
