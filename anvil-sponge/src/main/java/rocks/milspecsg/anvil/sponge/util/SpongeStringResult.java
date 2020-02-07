/*
 *   Anvil - MilSpecSG
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

package rocks.milspecsg.anvil.sponge.util;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import rocks.milspecsg.anvil.common.util.CommonStringResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpongeStringResult extends CommonStringResult<Text, CommandSource> {

    @Override
    public Builder<Text, CommandSource> builder() {
        return new SpongeStringResultBuilder();
    }

    @Override
    public void send(Text result, CommandSource commandSource) {
        commandSource.sendMessage(result);
    }

    @Override
    public Text deserialize(String text) {
        return TextSerializers.FORMATTING_CODE.deserialize(text);
    }

    @Override
    public String serialize(Text text) {
        return TextSerializers.FORMATTING_CODE.serialize(text);
    }

    private static final class SpongeStringResultBuilder extends CommonStringResultBuilder<Text, CommandSource> {

        private final List<TextElement> elements;
        private HoverAction<?> hoverAction;
        private ClickAction<?> clickAction;

        private SpongeStringResultBuilder() {
            elements = new ArrayList<>();
        }

        @Override
        public Builder<Text, CommandSource> aqua() {
            elements.add(TextColors.AQUA);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> black() {
            elements.add(TextColors.BLACK);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> blue() {
            elements.add(TextColors.BLUE);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> dark_aqua() {
            elements.add(TextColors.DARK_AQUA);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> dark_blue() {
            elements.add(TextColors.DARK_BLUE);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> dark_gray() {
            elements.add(TextColors.DARK_GRAY);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> dark_green() {
            elements.add(TextColors.DARK_GREEN);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> dark_purple() {
            elements.add(TextColors.DARK_PURPLE);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> dark_red() {
            elements.add(TextColors.DARK_RED);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> gold() {
            elements.add(TextColors.GOLD);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> gray() {
            elements.add(TextColors.GRAY);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> green() {
            elements.add(TextColors.GREEN);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> light_purple() {
            elements.add(TextColors.LIGHT_PURPLE);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> red() {
            elements.add(TextColors.RED);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> reset() {
            elements.add(TextColors.RESET);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> white() {
            elements.add(TextColors.WHITE);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> yellow() {
            elements.add(TextColors.YELLOW);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> append(Object... content) {
            for (Object o : content) {
                if (o instanceof Builder) {
                    elements.add(((Builder<Text, CommandSource>) o).build());
                } else if (o instanceof TextElement) {
                    elements.add((TextElement) o);
                } else {
                    elements.add(Text.of(o));
                }
            }
            return this;
        }

        @Override
        public Builder<Text, CommandSource> appendJoining(Object delimiter, Object... content) {
            final int indexOfLast = content.length - 1;
            for (int i = 0; i <= indexOfLast; i++) {
                Object o = content[i];
                if (o instanceof Builder) {
                    elements.add(((Builder<Text, CommandSource>) o).build());
                } else if (o instanceof TextElement) {
                    elements.add((TextElement) o);
                } else {
                    elements.add(Text.of(o));
                }
                if (i != indexOfLast) {
                    elements.add(Text.of(delimiter));
                }
            }
            return this;
        }

        @Override
        public Builder<Text, CommandSource> onHoverShowText(Text content) {
            hoverAction = TextActions.showText(content);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> onHoverShowText(Builder<Text, CommandSource> builder) {
            return onHoverShowText(builder.build());
        }

        @Override
        public Builder<Text, CommandSource> onClickSuggestCommand(String command) {
            clickAction = TextActions.suggestCommand(command);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> onClickRunCommand(String command) {
            clickAction = TextActions.runCommand(command);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> onClickExecuteCallback(Consumer<CommandSource> callback) {
            clickAction = TextActions.executeCallback(callback);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> onClickOpenUrl(URL url) {
            clickAction = TextActions.openUrl(url);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> onClickOpenUrl(String url) {
            try {
                clickAction = TextActions.openUrl(new URL(url));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return this;
        }

        @Override
        public Text build() {
            Text.Builder builder = Text.builder().append(Text.of(elements.toArray()));
            if (hoverAction != null) {
                builder.onHover(hoverAction);
            }
            if (clickAction != null) {
                builder.onClick(clickAction);
            }
            return builder.build();
        }

        @Override
        public void sendTo(CommandSource commandSource) {
            commandSource.sendMessage(build());
        }
    }
}
