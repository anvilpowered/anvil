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

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Platform;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.common.command.CommonCallbackCommand;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CommonTextService< TCommandSource>
    implements TextService< TCommandSource> {

    @Inject
    private Platform platform;

    @Inject
    private PluginInfo pluginInfo;

    @Inject
    protected CommonCallbackCommand< TCommandSource> callbackCommand;

    @Override
    public Component success(String s) {
        return builder().green().append(s).build();
    }

    @Override
    public Component fail(String s) {
        return builder().red().append(s).build();
    }

    @Override
    public PaginationBuilder< TCommandSource> paginationBuilder() {
        return new CommonExtendedPaginationBuilder();
    }

    @Override
    public void send(Component text, TCommandSource receiver, UUID sourceUUID) {
        send(text, receiver);
    }

    @Override
    public void send(Component text, TCommandSource receiver, Object source) {
        send(text, receiver);
    }

    @Override
    public String toPlain(String text) {
        return text.replaceAll("&[0-9a-fklmnor]", "");
    }

    private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\r\n|\r|\n");

    @Override
    public int lineCount(Component text) {
        if (text == null) {
            return -1;
        }
        String s = serializePlain(text);
        if (s.isEmpty()) {
            return 0;
        }
        Matcher m = LINE_BREAK_PATTERN.matcher(s);
        int lines = 1;
        while (m.find()) {
            lines++;
        }
        return lines;
    }

    protected abstract class CommonTextBuilder
        implements Builder< TCommandSource> {

        @Nullable
        protected Consumer<TCommandSource> callback;

        @Override
        public Builder< TCommandSource> append(CharSequence... contents) {
            return append((Object[]) contents);
        }

        @Override
        public Builder< TCommandSource> appendCount(
            int count, Object... contents) {
            for (int i = 0; i < count; i++) {
                append(contents);
            }
            return this;
        }

        @Override
        public Builder< TCommandSource> appendCount(
            int count, CharSequence... contents) {
            for (int i = 0; i < count; i++) {
                append(contents);
            }
            return this;
        }

        protected Builder< TCommandSource> appendWithPadding(
            @Nullable BiConsumer<Integer, Component> before,
            @Nullable BiConsumer<Integer, Component> after,
            int width, Object padding, Object... contents) {
            if (width < 1) {
                throw new IllegalArgumentException("Width must be at least 1");
            }
            final Component paddingText = of(padding);
            final int paddingLength = length(paddingText);
            if (paddingLength < 1) {
                throw new IllegalArgumentException("Padding length must be at least 1");
            } else if (width < paddingLength) {
                throw new IllegalArgumentException("Padding length must not be greater than width");
            }
            final Component contentsText = of(contents);
            final int contentsLength = length(contentsText);
            if (width < contentsLength) {
                throw new IllegalArgumentException("Contents length must not be greater than width");
            }
            int missingSpace = (width - contentsLength) / paddingLength;
            boolean add = missingSpace != 0;
            if (add && before != null) {
                before.accept(missingSpace, paddingText);
            }
            append(contentsText);
            if (add && after != null) {
                after.accept(missingSpace, paddingText);
            }
            return this;
        }

        @Override
        public Builder< TCommandSource> appendWithPaddingLeft(int width, Object padding, Object... contents) {
            return appendWithPadding(this::appendCount, null, width, padding, contents);
        }

        @Override
        public Builder< TCommandSource> appendWithPaddingLeft(int width, Object padding, CharSequence... contents) {
            return appendWithPaddingLeft(width, padding, (Object[]) contents);
        }

        @Override
        public Builder< TCommandSource> appendWithPaddingAround(int width, Object padding, Object... contents) {
            BiConsumer<Integer, Component> bothEnds = (m, c) -> appendCount(m / 2, c);
            return appendWithPadding(bothEnds, bothEnds, width, padding, contents);
        }

        @Override
        public Builder< TCommandSource> appendWithPaddingAround(int width, Object padding, CharSequence... contents) {
            return appendWithPaddingAround(width, padding, (Object[]) contents);
        }

        @Override
        public Builder< TCommandSource> appendWithPaddingRight(int width, Object padding, Object... contents) {
            return appendWithPadding(null, this::appendCount, width, padding, contents);
        }

        @Override
        public Builder< TCommandSource> appendWithPaddingRight(int width, Object padding, CharSequence... contents) {
            return appendWithPaddingRight(width, padding, (Object[]) contents);
        }

        @Override
        public Builder< TCommandSource> appendIf(
            boolean condition, Object... contents) {
            return condition ? append(contents) : this;
        }

        @Override
        public Builder< TCommandSource> appendIf(
            boolean condition, CharSequence... contents) {
            return condition ? append(contents) : this;
        }

        @Override
        public Builder< TCommandSource> appendJoining(
            Object delimiter, CharSequence... contents) {
            return appendJoining(delimiter, (Object[]) contents);
        }

        @Override
        public Builder< TCommandSource> appendJoiningIf(
            boolean condition, Object delimiter, Object... contents) {
            return condition ? appendJoining(delimiter, contents) : this;
        }

        @Override
        public Builder< TCommandSource> appendJoiningIf(
            boolean condition, Object delimiter, CharSequence... contents) {
            return condition ? appendJoining(delimiter, contents) : this;
        }

        @Override
        public Builder< TCommandSource> appendPrefix() {
            return append(pluginInfo.getPrefix());
        }

        @Override
        public Builder< TCommandSource> onHoverShowText(
            Builder< TCommandSource> builder) {
            return onHoverShowText(builder.build());
        }

        @Override
        public Builder< TCommandSource> onClickExecuteCallback(
            Consumer<TCommandSource> callback) {
            this.callback = callback;
            return this;
        }

        protected void initializeCallback() {
            UUID uuid = UUID.randomUUID();
            callbackCommand.addCallback(uuid, callback);
            String platform = Anvil.getPlatform().getName();
            String command;
            switch (platform) {
                case "bungee":
                    command = "/anvilb:callback ";
                    break;
                case "velocity":
                    command = "/anvilv:callback ";
                    break;
                default:
                    command = "/anvil:callback ";
                    break;
            }
            onClickRunCommand(command + uuid);
        }

        @Override
        public void sendTo(TCommandSource receiver) {
            send(build(), receiver);
        }

        @Override
        public void sendToConsole() {
            CommonTextService.this.sendToConsole(build());
        }
    }

    protected abstract class CommonPaginationBuilder
        implements PaginationBuilder< TCommandSource> {

        @Override
        public PaginationBuilder< TCommandSource> title(
            @Nullable Builder< TCommandSource> title) {
            if (title == null) {
                return title((Component) null);
            }
            return title(title.build());
        }

        @Override
        public PaginationBuilder< TCommandSource> header(
            @Nullable Builder< TCommandSource> header) {
            if (header == null) {
                return header((Component) null);
            }
            return header(header.build());
        }

        @Override
        public PaginationBuilder< TCommandSource> footer(
            @Nullable Builder< TCommandSource> footer) {
            if (footer == null) {
                return footer((Component) null);
            }
            return footer(footer.build());
        }

        @Override
        public PaginationBuilder< TCommandSource> padding(
            Builder< TCommandSource> padding) {
            return padding(padding.build());
        }
    }

    protected class CommonExtendedPaginationBuilder
        extends CommonPaginationBuilder {

        List<Component> contents;
        @Nullable
        Component title;
        @Nullable
        Component header;
        @Nullable
        Component footer;
        Component padding;
        int linesPerPage;

        public CommonExtendedPaginationBuilder() {
            padding = builder().dark_green().append("-").build();
            linesPerPage = 20;
        }

        @Override
        public PaginationBuilder< TCommandSource> contents(
            Component... contents) {
            this.contents = Arrays.asList(contents);
            return this;
        }

        @Override
        public PaginationBuilder< TCommandSource> contents(
            Iterable<Component> contents) {
            this.contents = new ArrayList<>(64);
            contents.forEach(c -> this.contents.add(c));
            return this;
        }

        @Override
        public PaginationBuilder< TCommandSource> title(
            @Nullable Component title) {
            this.title = title;
            return this;
        }

        @Override
        public PaginationBuilder<TCommandSource> header(
            @Nullable Component header) {
            this.header = header;
            return this;
        }

        @Override
        public PaginationBuilder< TCommandSource> footer(
            @Nullable Component footer) {
            this.footer = footer;
            return this;
        }

        @Override
        public PaginationBuilder< TCommandSource> padding(
            Component padding) {
            this.padding = padding;
            return this;
        }

        @Override
        public PaginationBuilder< TCommandSource> linesPerPage(
            int linesPerPage) {
            if (linesPerPage < 1) {
                throw new IllegalArgumentException("Lines per page must be at least 1");
            }
            this.linesPerPage = linesPerPage;
            return this;
        }

        @Override
        public Pagination< TCommandSource> build() {
            return new CommonExtendedPagination(
                contents,
                title,
                header,
                footer,
                padding,
                linesPerPage
            );
        }
    }

    protected class CommonExtendedPagination
        implements Pagination< TCommandSource> {

        protected final List<Component> contents;

        @Nullable
        protected final Component title;

        @Nullable
        protected final Component header;

        @Nullable
        protected final Component footer;

        protected final Component padding;

        protected final int linesPerPage;

        @Nullable
        protected List<Component> pages;

        protected CommonExtendedPagination(
            List<Component> contents,
            @Nullable Component title,
            @Nullable Component header,
            @Nullable Component footer,
            Component padding,
            int linesPerPage
        ) {
            this.contents = Preconditions.checkNotNull(contents, "contents");
            this.title = title;
            this.header = header;
            this.footer = footer;
            this.padding = Preconditions.checkNotNull(padding, "padding");
            this.linesPerPage = linesPerPage;
        }

        @Override
        public Iterable<Component> getContents() {
            return contents;
        }

        @Override
        public Optional<Component> getTitle() {
            return Optional.ofNullable(title);
        }

        @Override
        public Optional<Component> getHeader() {
            return Optional.ofNullable(header);
        }

        @Override
        public Optional<Component> getFooter() {
            return Optional.ofNullable(footer);
        }

        @Override
        public Component getPadding() {
            return padding;
        }

        @Override
        public int getLinesPerPage() {
            return linesPerPage;
        }

        protected void buildPages() {
            final int LINE_WIDTH;
            if (platform.getName().equals("sponge")) {
                LINE_WIDTH = 91;
            } else {
                LINE_WIDTH = 51;
            }
            pages = new ArrayList<>();
            int contentsIndex = 0; // index in contents list
            final int contentsSize = contents.size();
            boolean isFirstPage = true;
            outer:
            while (contentsIndex < contentsSize) {
                Builder< TCommandSource> page = builder();
                int linesAvailable = linesPerPage - 1;
                if (title != null) {
                    page.appendWithPaddingAround(LINE_WIDTH, ' ', title).append("\n");
                    linesAvailable -= lineCount(title);
                }
                if (header != null) {
                    page.appendWithPaddingAround(LINE_WIDTH, ' ', header).append("\n");
                    linesAvailable -= lineCount(header);
                } else {
                    page.appendWithPaddingAround(LINE_WIDTH, padding).append("\n");
                }
                boolean withFooter = false;
                if (footer != null) {
                    // reserve space for footer
                    // will be added later
                    withFooter = true;
                    linesAvailable -= lineCount(footer);
                }
                for (; linesAvailable > 0; --linesAvailable) {
                    // check if there are any contents left
                    if (contentsIndex >= contentsSize) {
                        // dont add empty lines on first page
                        if (isFirstPage) {
                            break;
                        }
                        // no more content, add an empty line
                        page.append("\n");
                    }
                    Component next = contents.get(contentsIndex);
                    // make sure there's enough space
                    if (linesAvailable < lineCount(next)) {
                        continue outer;
                    }
                    page.append(next, "\n");
                    ++contentsIndex;
                }
                if (isFirstPage) {
                    isFirstPage = false;
                }
                if (withFooter) {
                    page.appendWithPaddingAround(LINE_WIDTH, padding, footer);
                } else {
                    page.appendWithPaddingAround(LINE_WIDTH, padding);
                }
                pages.add(page.build());
            }
        }

        @Override
        public void sendTo(TCommandSource receiver) {
            if (pages == null) {
                buildPages();
            }
            if (pages.size() == 0) {
                return;
            }
            send(pages.get(0), receiver);
        }

        @Override
        public void sendToConsole() {
            sendTo(getConsole());
        }
    }
}
