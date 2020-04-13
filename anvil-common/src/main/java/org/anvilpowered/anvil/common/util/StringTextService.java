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

package org.anvilpowered.anvil.common.util;

import java.net.URL;
import java.util.function.Consumer;

public abstract class StringTextService<TCommandSource>
    extends CommonTextService<String, TCommandSource> {

    @Override
    public String deserialize(String text) {
        return text;
    }

    @Override
    public String serializePlain(String text) {
        return toPlain(text);
    }

    @Override
    public String serialize(String text) {
        return text;
    }

    protected class StringTextBuilder extends CommonTextBuilder {
        protected final StringBuilder elements;

        public StringTextBuilder() {
            elements = new StringBuilder();
        }

        @Override
        public Builder<String, TCommandSource> aqua() {
            elements.append("\u00a7b");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> black() {
            elements.append("\u00a70");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> blue() {
            elements.append("\u00a79");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> dark_aqua() {
            elements.append("\u00a73");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> dark_blue() {
            elements.append("\u00a71");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> dark_gray() {
            elements.append("\u00a78");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> dark_green() {
            elements.append("\u00a72");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> dark_purple() {
            elements.append("\u00a75");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> dark_red() {
            elements.append("\u00a74");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> gold() {
            elements.append("\u00a76");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> gray() {
            elements.append("\u00a77");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> green() {
            elements.append("\u00a7a");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> light_purple() {
            elements.append("\u00a7d");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> red() {
            elements.append("\u00a7c");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> reset() {
            elements.append("\u00a7r");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> white() {
            elements.append("\u00a7f");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> yellow() {
            elements.append("\u00a7e");
            return this;
        }

        @Override
        public Builder<String, TCommandSource> append(Object... contents) {
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
        public Builder<String, TCommandSource> appendJoining(Object delimiter, Object... contents) {
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
        public Builder<String, TCommandSource> onHoverShowText(String content) {
            return this;
        }

        @Override
        public Builder<String, TCommandSource> onClickSuggestCommand(String command) {
            return this;
        }

        @Override
        public Builder<String, TCommandSource> onClickRunCommand(String command) {
            return this;
        }

        @Override
        public Builder<String, TCommandSource> onClickExecuteCallback(Consumer<TCommandSource> callback) {
            return this;
        }

        @Override
        public Builder<String, TCommandSource> onClickOpenUrl(URL url) {
            return this;
        }

        @Override
        public Builder<String, TCommandSource> onClickOpenUrl(String url) {
            return this;
        }

        @Override
        public String build() {
            return elements.toString();
        }
    }
}
