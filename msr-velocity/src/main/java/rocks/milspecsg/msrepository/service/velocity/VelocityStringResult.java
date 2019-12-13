/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msrepository.service.velocity;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import rocks.milspecsg.msrepository.api.tools.resultbuilder.StringResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VelocityStringResult extends VelocityResult<String> implements StringResult<TextComponent, CommandSource> {

    @Override
    public Builder<TextComponent, CommandSource> builder() {
        return new VelocityStringResultBuilder();
    }

    @Override
    public void send(TextComponent textComponent, CommandSource commandSource) {
        commandSource.sendMessage(textComponent);
    }

    private static final class VelocityStringResultBuilder implements Builder<TextComponent, CommandSource> {

        private final TextComponent.Builder builder;

        private VelocityStringResultBuilder() {
            builder = TextComponent.builder();
        }

        @Override
        public Builder<TextComponent, CommandSource> aqua() {
            builder.color(TextColor.AQUA);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> black() {
            builder.color(TextColor.BLACK);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> blue() {
            builder.color(TextColor.BLUE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_aqua() {
            builder.color(TextColor.DARK_AQUA);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_blue() {
            builder.color(TextColor.DARK_BLUE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_gray() {
            builder.color(TextColor.DARK_GRAY);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_green() {
            builder.color(TextColor.DARK_GREEN);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_purple() {
            builder.color(TextColor.DARK_PURPLE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> dark_red() {
            builder.color(TextColor.DARK_RED);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> gold() {
            builder.color(TextColor.GOLD);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> gray() {
            builder.color(TextColor.GRAY);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> green() {
            builder.color(TextColor.GREEN);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> light_purple() {
            builder.color(TextColor.LIGHT_PURPLE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> red() {
            builder.color(TextColor.RED);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> reset() {
            builder.color(TextColor.WHITE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> white() {
            builder.color(TextColor.WHITE);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> yellow() {
            builder.color(TextColor.YELLOW);
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> append(Object... content) {
            for (Object o : content) {
                if (o instanceof Builder) {
                    builder.append(((Builder<TextComponent, CommandSource>) o).build());
                } else if (o instanceof TextComponent) {
                    builder.append((TextComponent) o);
                } else if (o instanceof String) {
                    builder.append(String.valueOf(o));
                } else {
                    builder.append(TextComponent.of(o.toString()));
                }
            }
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> onHoverShowText(TextComponent content) {
            builder.hoverEvent(HoverEvent.showText(content));
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> onHoverShowText(Builder<TextComponent, CommandSource> builder) {
            return onHoverShowText(builder.build());
        }

        @Override
        public Builder<TextComponent, CommandSource> onClickSuggestCommand(String command) {
            builder.clickEvent(ClickEvent.suggestCommand(command));
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> onClickRunCommand(String command) {
            builder.clickEvent(ClickEvent.runCommand(command));
            return this;
        }

        @Override
        public Builder<TextComponent, CommandSource> onClickExecuteCallback(Consumer<CommandSource> callback) {
            //TODO Waiting for reply
            //builder.clickEvent(ClickEvent.of(callback));
            throw new UnsupportedOperationException();
        }

        @Override
        public TextComponent build() {
            return builder.build();
        }

        @Override
        public void sendTo(CommandSource commandSource) {
            commandSource.sendMessage(build());
        }
    }
}
