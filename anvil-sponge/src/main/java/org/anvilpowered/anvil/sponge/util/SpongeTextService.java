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

package org.anvilpowered.anvil.sponge.util;

import org.anvilpowered.anvil.common.util.CommonTextService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class SpongeTextService extends CommonTextService<Text, CommandSource> {

    @Override
    public Builder<Text, CommandSource> builder() {
        return new SpongeTextBuilder();
    }

    @Override
    public PaginationBuilder<Text, CommandSource> paginationBuilder() {
        return new SpongePaginationBuilder();
    }

    @Override
    public void send(Text text, CommandSource receiver) {
        receiver.sendMessage(text);
    }

    @Override
    public CommandSource getConsole() {
        return Sponge.getServer().getConsole();
    }

    @Override
    public Text deserialize(String text) {
        return TextSerializers.FORMATTING_CODE.deserialize(text);
    }

    @Override
    public String serialize(Text text) {
        return TextSerializers.FORMATTING_CODE.serialize(text);
    }

    @Override
    public String serializePlain(Text text) {
        return text.toPlain();
    }

    protected class SpongeTextBuilder extends CommonTextBuilder {

        private final List<TextElement> elements;
        private HoverAction<?> hoverAction;
        private ClickAction<?> clickAction;

        protected SpongeTextBuilder() {
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
        public Builder<Text, CommandSource> bold() {
            elements.add(TextStyles.BOLD);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> italic() {
            elements.add(TextStyles.ITALIC);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> obfuscated() {
            elements.add(TextStyles.OBFUSCATED);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> strikethrough() {
            elements.add(TextStyles.STRIKETHROUGH);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> underlined() {
            elements.add(TextStyles.UNDERLINE);
            return this;
        }

        @Override
        public Builder<Text, CommandSource> append(Object... contents) {
            for (Object o : contents) {
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
        public Builder<Text, CommandSource> appendJoining(Object delimiter, Object... contents) {
            final int indexOfLast = contents.length - 1;
            for (int i = 0; i <= indexOfLast; i++) {
                Object o = contents[i];
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
        public Builder<Text, CommandSource> onClickExecuteCallback(
            Consumer<CommandSource> callback) {
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
            boolean hover = hoverAction != null;
            boolean click = clickAction != null;

            if (elements.size() == 1 && !hover && !click) {
                TextElement o = elements.get(0);
                if (o instanceof Builder) {
                    return ((Builder<Text, CommandSource>) o).build();
                }
                return Text.of(o);
            }
            Text.Builder builder = Text.builder().append(Text.of(elements.toArray()));
            if (hover) {
                builder.onHover(hoverAction);
            }
            if (click) {
                builder.onClick(clickAction);
            }
            return builder.build();
        }
    }

    protected class SpongePaginationBuilder extends CommonPaginationBuilder {

        private final PaginationList.Builder builder;

        public SpongePaginationBuilder() {
            builder = Sponge.getServiceManager()
                .provideUnchecked(PaginationService.class).builder();
        }

        @Override
        public PaginationBuilder<Text, CommandSource> contents(
            Text... contents) {
            builder.contents(contents);
            return this;
        }

        @Override
        public PaginationBuilder<Text, CommandSource> contents(
            Iterable<Text> contents) {
            builder.contents(contents);
            return this;
        }

        @Override
        public PaginationBuilder<Text, CommandSource> title(
            @Nullable Text title) {
            builder.title(title);
            return this;
        }

        @Override
        public PaginationBuilder<Text, CommandSource> header(
            @Nullable Text header) {
            builder.header(header);
            return this;
        }

        @Override
        public PaginationBuilder<Text, CommandSource> footer(
            @Nullable Text footer) {
            builder.footer(footer);
            return this;
        }

        @Override
        public PaginationBuilder<Text, CommandSource> padding(
            Text padding) {
            builder.padding(padding);
            return this;
        }

        @Override
        public PaginationBuilder<Text, CommandSource> linesPerPage(
            int linesPerPage) {
            if (linesPerPage < 1) {
                throw new IllegalArgumentException("Lines per page must be at least 1");
            }
            builder.linesPerPage(linesPerPage);
            return this;
        }

        @Override
        public Pagination<Text, CommandSource> build() {
            return new SpongePagination(builder.build());
        }
    }

    protected static class SpongePagination implements Pagination<Text, CommandSource> {

        private final PaginationList paginationList;

        public SpongePagination(PaginationList paginationList) {
            this.paginationList = paginationList;
        }

        @Override
        public Iterable<Text> getContents() {
            return paginationList.getContents();
        }

        @Override
        public Optional<Text> getTitle() {
            return paginationList.getTitle();
        }

        @Override
        public Optional<Text> getHeader() {
            return paginationList.getHeader();
        }

        @Override
        public Optional<Text> getFooter() {
            return paginationList.getFooter();
        }

        @Override
        public Text getPadding() {
            return paginationList.getPadding();
        }

        @Override
        public int getLinesPerPage() {
            return paginationList.getLinesPerPage();
        }

        @Override
        public void sendTo(CommandSource receiver) {
            paginationList.sendTo(receiver);
        }

        @Override
        public void sendToConsole() {
            sendTo(Sponge.getServer().getConsole());
        }
    }
}
