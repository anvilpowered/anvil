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

package org.anvilpowered.anvil.api.util;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface TextService<TString, TCommandSource>
    extends Result<TString, String> {

    /**
     * @return A new {@link Builder} instance
     */
    Builder<TString, TCommandSource> builder();

    /**
     * @return A new {@link PaginationBuilder} instance
     */
    PaginationBuilder<TString, TCommandSource> paginationBuilder();

    /**
     * Create a {@link TString} with the provided contents
     *
     * @param contents The contents to add to the result
     * @return A built {@link TString}
     */
    default TString of(Object... contents) {
        return builder().append(contents).build();
    }

    /**
     * Create a {@link TString} with the provided contents
     *
     * @param contents The contents to add to the result
     * @return A built {@link TString}
     */
    default TString of(CharSequence... contents) {
        return builder().append(contents).build();
    }

    /**
     * Sends the provided {@link TString text} to the provided {@link TCommandSource receiver}.
     *
     * @param text     The {@link TString text} to send
     * @param receiver The {@link TCommandSource receiver} to send the provided text to
     */
    void send(TString text, TCommandSource receiver);

    /**
     * Sends the provided {@link TString text} to the provided {@link TCommandSource receiver},
     * originating from the provided {@link UUID sourceUUID}.
     *
     * @param text       The {@link TString text} to send
     * @param receiver   The {@link TCommandSource receiver} to send the provided text to
     * @param sourceUUID The {@link UUID} of the source of the message
     */
    void send(TString text, TCommandSource receiver, UUID sourceUUID);

    /**
     * Sends the provided {@link TString text} to the provided {@link TCommandSource receiver},
     * originating from the provided source. Attempts to extract a {@link UUID}
     * from the provided source to server as the identity.
     *
     * @param text     The {@link TString text} to send
     * @param receiver The {@link TCommandSource receiver} source to send the provided text to
     * @param source   The source of the message
     */
    void send(TString text, TCommandSource receiver, Object source);

    /**
     * Send the provided {@link TString text} to the console
     *
     * @param text The {@link TString text} to send
     */
    default void sendToConsole(TString text) {
        send(text, getConsole());
    }

    /**
     * @return The server console
     */
    TCommandSource getConsole();

    /**
     * Deserializes the provided {@link String} using the
     * character {@literal '&'} to determine styles.
     *
     * @param text {@link String} text to deserialize
     * @return The {@link TString} result of the deserialization
     */
    TString deserialize(String text);

    /**
     * Serializes the provided {@link String} using the
     * character {@literal '&'} to serialize styles.
     *
     * @param text {@link TString} text to serialize
     * @return The {@link String} result of the serialization
     */
    String serialize(TString text);

    /**
     * Serializes the provided {@link String} and
     * ignores all styles.
     *
     * @param text {@link TString} text to serialize
     * @return The {@link String} result of the serialization
     */
    String serializePlain(TString text);

    /**
     * Removes all styles codes from the provided {@link String}
     * using the character {@literal '&'} to determine styles
     *
     * <p>
     * For example, {@code "&l&bhello &aworld"} becomes {@code "hello world"}
     * </p>
     *
     * @param text {@link String} text to remove color from
     * @return The provided {@link String} without any color codes
     */
    String toPlain(String text);

    /**
     * Counts the number of lines in the provided {@link TString}
     *
     * @param text {@link TString} text to count lines
     * @return The number of lines in the provided {@link TString}.
     * -1 if null. 0 if empty.
     */
    int lineCount(@Nullable TString text);

    /**
     * Counts the number of characters in the provided {@link TString}
     * excluding color codes.
     *
     * @param text {@link TString} text to count characters
     * @return The number of characters in the provided {@link TString}.
     * -1 if null. 0 if empty.
     */
    default int length(@Nullable TString text) {
        return text == null
            ? -1
            : serializePlain(text).length();
    }

    interface Builder<TString, TCommandSource> {

        /**
         * Sets the current color to aqua. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> aqua();

        /**
         * Sets the current color to black. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> black();

        /**
         * Sets the current color to blue. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> blue();

        /**
         * Sets the current color to dark aqua. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> dark_aqua();

        /**
         * Sets the current color to dark blue. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> dark_blue();

        /**
         * Sets the current color to dark gray. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> dark_gray();

        /**
         * Sets the current color to dark green. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> dark_green();

        /**
         * Sets the current color to dark purple. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> dark_purple();

        /**
         * Sets the current color to dark red. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> dark_red();

        /**
         * Sets the current color to gold. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> gold();

        /**
         * Sets the current color to gray. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> gray();

        /**
         * Sets the current color to green. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> green();

        /**
         * Sets the current color to light purple. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> light_purple();

        /**
         * Sets the current color to red. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> red();

        /**
         * Resets the current style to default. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> reset();

        /**
         * Sets the current color to white. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> white();

        /**
         * Sets the current color to yellow. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> yellow();

        /**
         * Sets the current style to bold. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> bold();

        /**
         * Sets the current style to italic. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> italic();

        /**
         * Sets the current style to obfuscated. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> obfuscated();

        /**
         * Sets the current style to strikethrough. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> strikethrough();

        /**
         * Sets the current style to underlined. Only applies
         * to text appended after this call.
         *
         * @return {@code this}
         */
        Builder<TString, TCommandSource> underlined();

        /**
         * Append the provided contents to this builder
         *
         * @param contents {@link Object} contents to append
         * @return {@code this}
         */
        Builder<TString, TCommandSource> append(
            Object... contents);

        /**
         * Append the provided contents to this builder
         *
         * @param contents {@link CharSequence} contents to append
         * @return {@code this}
         */
        Builder<TString, TCommandSource> append(
            CharSequence... contents);

        /**
         * Append the provided contents to this builder
         * {@code count} times.
         *
         * <p>
         * This method is the same as:
         * </p>
         * <pre>{@code
         * for (int i = 0; i < count; i++) {
         *     builder.append(contents);
         * }
         * }</pre>
         *
         * @param count    number of times to append the provided contents
         * @param contents {@link Object} contents to append
         * @return {@code this}
         */
        Builder<TString, TCommandSource> appendCount(
            int count, Object... contents);

        /**
         * Append the provided contents to this builder
         * {@code count} times.
         *
         * <p>
         * This method is the same as:
         * </p>
         * <pre>{@code
         * for (int i = 0; i < count; i++) {
         *     builder.append(contents);
         * }
         * }</pre>
         *
         * @param count    number of times to append the provided contents
         * @param contents {@link CharSequence} contents to append
         * @return {@code this}
         */
        Builder<TString, TCommandSource> appendCount(
            int count, CharSequence... contents);

        /**
         * Append the provided contents to this builder
         * and add the provided padding on the left side
         * until the result is <strong>at most</strong> {@code width} wide.
         *
         * <p>
         * An example of usage is:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingLeft(15, '-', "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "----------hello"}
         * </p>
         * <br>
         * <p>
         * If the value for padding is longer than one character,
         * the appended result may not reach the full provided {@code width}.
         * This can be seen in the following example:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingLeft(15, "abc", "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "abcabcabchello"} (14 characters instead of 15)
         * </p>
         * <br>
         * <p>
         * This happens because the remaining width is not a multiple of
         * the length of the provided padding, which makes it impossible fill
         * the full length of the appended result.
         * </p>
         * <br>
         * <p>
         * Note: The appended result will be at most {@code width} wide, or
         * an exception will be thrown
         * </p>
         *
         * @param width    The maximum total width after padding
         * @param padding  The padding to fill the left side with
         * @param contents The contents to append
         * @return {@code this}
         * @throws IllegalArgumentException If width {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal >} width
         * @throws IllegalArgumentException If contents length {@literal >} width
         */
        Builder<TString, TCommandSource> appendWithPaddingLeft(
            int width, Object padding, Object... contents);

        /**
         * Append the provided contents to this builder
         * and add the provided padding on the left side
         * until the result is <strong>at most</strong> {@code width} wide.
         *
         * <p>
         * An example of usage is:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingLeft(15, '-', "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "----------hello"}
         * </p>
         * <br>
         * <p>
         * If the value for padding is longer than one character,
         * the appended result may not reach the full provided {@code width}.
         * This can be seen in the following example:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingLeft(15, "abc", "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "abcabcabchello"} (14 characters instead of 15)
         * </p>
         * <br>
         * <p>
         * This happens because the remaining width is not a multiple of
         * the length of the provided padding, which makes it impossible fill
         * the full length of the appended result.
         * </p>
         * <br>
         * <p>
         * Note: The appended result will be at most {@code width} wide, or
         * an exception will be thrown
         * </p>
         *
         * @param width    The maximum total width after padding
         * @param padding  The padding to fill the left side with
         * @param contents The contents to append
         * @return {@code this}
         * @throws IllegalArgumentException If width {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal >} width
         * @throws IllegalArgumentException If contents length {@literal >} width
         */
        Builder<TString, TCommandSource> appendWithPaddingLeft(
            int width, Object padding, CharSequence... contents);

        /**
         * Append the provided contents to this builder
         * and add the provided padding on both the left and right side
         * until the result is <strong>at most</strong> {@code width} wide.
         *
         * <p>
         * An example of usage is:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingAround(15, '-', "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "-----hello-----"}
         * </p>
         * <br>
         * <p>
         * If the value for padding is longer than one character,
         * the appended result may not reach the full provided {@code width}.
         * This can be seen in the following example:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingAround(15, "abc", "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "abchelloabc"} (11 characters instead of 15)
         * </p>
         * <br>
         * <p>
         * This happens because the remaining width is not a multiple of
         * the length of the provided padding, which makes it impossible fill
         * the full length of the appended result.
         * </p>
         * <br>
         * <p>
         * Note: The appended result will be at most {@code width} wide, or
         * an exception will be thrown
         * Note: The amount of padding on the left and right side will
         * always be the same
         * </p>
         *
         * @param width    The maximum total width after padding
         * @param padding  The padding to fill the left and right side with
         * @param contents The contents to append
         * @return {@code this}
         * @throws IllegalArgumentException If width {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal >} width
         * @throws IllegalArgumentException If contents length {@literal >} width
         */
        Builder<TString, TCommandSource> appendWithPaddingAround(
            int width, Object padding, Object... contents);

        /**
         * Append the provided contents to this builder
         * and add the provided padding on both the left and right side
         * until the result is <strong>at most</strong> {@code width} wide.
         *
         * <p>
         * An example of usage is:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingAround(15, '-', "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "-----hello-----"}
         * </p>
         * <br>
         * <p>
         * If the value for padding is longer than one character,
         * the appended result may not reach the full provided {@code width}.
         * This can be seen in the following example:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingAround(15, "abc", "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "abchelloabc"} (11 characters instead of 15)
         * </p>
         * <br>
         * <p>
         * This happens because the remaining width is not a multiple of
         * the length of the provided padding, which makes it impossible fill
         * the full length of the appended result.
         * </p>
         * <br>
         * <p>
         * Note: The appended result will be at most {@code width} wide, or
         * an exception will be thrown
         * Note: The amount of padding on the left and right side will
         * always be the same
         * </p>
         *
         * @param width    The maximum total width after padding
         * @param padding  The padding to fill the left and right side with
         * @param contents The contents to append
         * @return {@code this}
         * @throws IllegalArgumentException If width {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal >} width
         * @throws IllegalArgumentException If contents length {@literal >} width
         */
        Builder<TString, TCommandSource> appendWithPaddingAround(
            int width, Object padding, CharSequence... contents);

        /**
         * Append the provided contents to this builder
         * and add the provided padding on the right side
         * until the result is <strong>at most</strong> {@code width} wide.
         *
         * <p>
         * An example of usage is:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingRight(15, '-', "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "hello----------"}
         * </p>
         * <br>
         * <p>
         * If the value for padding is longer than one character,
         * the appended result may not reach the full provided {@code width}.
         * This can be seen in the following example:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingRight(15, "abc", "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "helloabcabcabc"} (14 characters instead of 15)
         * </p>
         * <br>
         * <p>
         * This happens because the remaining width is not a multiple of
         * the length of the provided padding, which makes it impossible fill
         * the full length of the appended result.
         * </p>
         * <br>
         * <p>
         * Note: The appended result will be at most {@code width} wide, or
         * an exception will be thrown
         * </p>
         *
         * @param width    The maximum total width after padding
         * @param padding  The padding to fill the right side with
         * @param contents The contents to append
         * @return {@code this}
         * @throws IllegalArgumentException If width {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal >} width
         * @throws IllegalArgumentException If contents length {@literal >} width
         */
        Builder<TString, TCommandSource> appendWithPaddingRight(
            int width, Object padding, Object... contents);

        /**
         * Append the provided contents to this builder
         * and add the provided padding on the right side
         * until the result is <strong>at most</strong> {@code width} wide.
         *
         * <p>
         * An example of usage is:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingRight(15, '-', "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "hello----------"}
         * </p>
         * <br>
         * <p>
         * If the value for padding is longer than one character,
         * the appended result may not reach the full provided {@code width}.
         * This can be seen in the following example:
         * </p>
         * <pre>{@code
         * builder.appendWithPaddingRight(15, "abc", "hello");
         * }</pre>
         * <p>
         * which appends the result {@code "helloabcabcabc"} (14 characters instead of 15)
         * </p>
         * <br>
         * <p>
         * This happens because the remaining width is not a multiple of
         * the length of the provided padding, which makes it impossible fill
         * the full length of the appended result.
         * </p>
         * <br>
         * <p>
         * Note: The appended result will be at most {@code width} wide, or
         * an exception will be thrown
         * </p>
         *
         * @param width    The maximum total width after padding
         * @param padding  The padding to fill the right side with
         * @param contents The contents to append
         * @return {@code this}
         * @throws IllegalArgumentException If width {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal <} 1
         * @throws IllegalArgumentException If padding length {@literal >} width
         * @throws IllegalArgumentException If contents length {@literal >} width
         */
        Builder<TString, TCommandSource> appendWithPaddingRight(
            int width, Object padding, CharSequence... contents);

        /**
         * Append the provided contents if {@code condition} is true.
         *
         * <p>
         * This method is the same as:
         * </p>
         * <pre>{@code
         * if (condition) {
         *     builder.append(contents);
         * }
         * }</pre>
         *
         * @param condition The condition to check
         * @param contents  The contents to append
         * @return {@code this}
         */
        Builder<TString, TCommandSource> appendIf(
            boolean condition, Object... contents);

        /**
         * Append the provided contents if {@code condition} is true.
         *
         * <p>
         * This method is the same as:
         * </p>
         * <pre>{@code
         * if (condition) {
         *     builder.append(contents);
         * }
         * }</pre>
         *
         * @param condition The condition to check
         * @param contents  The contents to append
         * @return {@code this}
         */
        Builder<TString, TCommandSource> appendIf(
            boolean condition, CharSequence... contents);

        /**
         * Append the provided contents, putting the
         * provided delimiter between each element
         *
         * <pre>{@code
         * builder.appendJoining(", ", "Bob", "Alice", "Steven");
         * }</pre>
         * <p>
         * is the same as
         * </p>
         * <pre>{@code
         * builder.append("Bob", ", ", "Alice", ", ", "Steven");
         * }</pre>
         * <p>
         * They both append: {@code "Bob, Alice, Steven"}
         * </p>
         *
         * @param delimiter The delimiter to put between each element
         * @param contents  The contents to append
         * @return {@code this}
         */
        Builder<TString, TCommandSource> appendJoining(
            Object delimiter, Object... contents);

        /**
         * Append the provided contents, putting the
         * provided delimiter between each element
         *
         * <pre>{@code
         * builder.appendJoining(", ", "Bob", "Alice", "Steven");
         * }</pre>
         * <p>
         * is the same as
         * </p>
         * <pre>{@code
         * builder.append("Bob", ", ", "Alice", ", ", "Steven");
         * }</pre>
         * <p>
         * They both append: {@code "Bob, Alice, Steven"}
         * </p>
         *
         * @param delimiter The delimiter to put between each element
         * @param contents  The contents to append
         * @return {@code this}
         */
        Builder<TString, TCommandSource> appendJoining(
            Object delimiter, CharSequence... contents);

        /**
         * Append the provided contents if {@code condition} is true,
         * putting the provided delimiter between each element
         *
         * <p>
         * This is a combination of {@link #appendIf(boolean, Object...)}
         * and {@link #appendJoining(Object, Object...)}
         * </p>
         *
         * <pre>{@code
         * builder.appendJoiningIf(condition, ", ", "Bob", "Alice", "Steven");
         * }</pre>
         * <p>
         * is the same as
         * </p>
         * <pre>{@code
         * if (condition) {
         *     builder.append("Bob", ", ", "Alice", ", ", "Steven");
         * }
         * }</pre>
         * <p>
         * They both append: {@code "Bob, Alice, Steven"} if {@code condition} is true
         * </p>
         *
         * @param condition The condition to check before appending
         * @param delimiter The delimiter to put between each element
         * @param contents  The contents to append
         * @return {@code this}
         */
        Builder<TString, TCommandSource> appendJoiningIf(
            boolean condition, Object delimiter, Object... contents);

        /**
         * Append the provided contents if {@code condition} is true,
         * putting the provided delimiter between each element
         *
         * <p>
         * This is a combination of {@link #appendIf(boolean, CharSequence...)}
         * and {@link #appendJoining(Object, CharSequence...)}
         * </p>
         *
         * <pre>{@code
         * builder.appendJoiningIf(condition, ", ", "Bob", "Alice", "Steven");
         * }</pre>
         * <p>
         * is the same as
         * </p>
         * <pre>{@code
         * if (condition) {
         *     builder.append("Bob", ", ", "Alice", ", ", "Steven");
         * }
         * }</pre>
         * <p>
         * They both append: {@code "Bob, Alice, Steven"} if {@code condition} is true
         * </p>
         *
         * @param condition The condition to check before appending
         * @param delimiter The delimiter to put between each element
         * @param contents  The contents to append
         * @return {@code this}
         */
        Builder<TString, TCommandSource> appendJoiningIf(
            boolean condition, Object delimiter, CharSequence... contents);

        /**
         * Show the provided text as a tooltip while
         * the mouse is hovering over this text
         *
         * @param text The text to show on hover
         * @return {@code this}
         */
        Builder<TString, TCommandSource> onHoverShowText(
            TString text);

        /**
         * Show the provided text as a tooltip while
         * the mouse is hovering over this text
         *
         * @param builder The text to show on hover
         * @return {@code this}
         */
        Builder<TString, TCommandSource> onHoverShowText(
            Builder<TString, TCommandSource> builder);

        /**
         * Suggest the provided command to the provided{@link TCommandSource} when
         * they click This puts it in their chat bar but does not run the command.
         *
         * @param command The command to suggest
         * @return {@code this}
         */
        Builder<TString, TCommandSource> onClickSuggestCommand(
            String command);

        /**
         * Run the provided command as the provided {@link TCommandSource} when they click.
         *
         * @param command The command to run
         * @return {@code this}
         */
        Builder<TString, TCommandSource> onClickRunCommand(
            String command);

        /**
         * Run the provided callback, passing the provided {@link TCommandSource}, when they click.
         *
         * @param callback The callback to run
         * @return {@code this}
         */
        Builder<TString, TCommandSource> onClickExecuteCallback(
            Consumer<TCommandSource> callback);

        /**
         * Open a url in a browser when this text is clicked.
         *
         * @param url The url to open
         * @return {@code this}
         */
        Builder<TString, TCommandSource> onClickOpenUrl(
            URL url);

        /**
         * Open a url in a browser when this text is clicked.
         *
         * @param url The url to open
         * @return {@code this}
         */
        Builder<TString, TCommandSource> onClickOpenUrl(
            String url);

        /**
         * Creates a {@link TString} from this builder.
         *
         * @return The built {@link TString}
         */
        TString build();

        /**
         * Creates a {@link TString text} from this builder and sends it to the provided {@link TCommandSource receiver}
         *
         * @param receiver The {@link TCommandSource receiver} to send the {@link TString text} to
         */
        void sendTo(TCommandSource receiver);

        /**
         * Creates a {@link TString} from this builder and sends
         * it to the console
         */
        void sendToConsole();
    }

    interface PaginationBuilder<TString, TCommandSource> {

        /**
         * Sets the contents of this {@link PaginationBuilder}.
         *
         * @param contents The contents to set
         * @return {@code this}
         */
        PaginationBuilder<TString, TCommandSource> contents(
            TString... contents);

        /**
         * Sets the contents of this {@link PaginationBuilder}.
         *
         * @param contents The contents to set
         * @return {@code this}
         */
        PaginationBuilder<TString, TCommandSource> contents(
            Iterable<TString> contents);

        /**
         * Sets the title of this {@link PaginationBuilder}.
         *
         * @param title The title to set, should be exactly one line
         * @return {@code this}
         */
        PaginationBuilder<TString, TCommandSource> title(
            @Nullable TString title);

        /**
         * Sets the title of this {@link PaginationBuilder}.
         *
         * @param title The title to set, should be exactly one line
         * @return {@code this}
         */
        PaginationBuilder<TString, TCommandSource> title(
            @Nullable Builder<TString, TCommandSource> title);

        /**
         * Sets the header of this {@link PaginationBuilder}.
         *
         * <p>
         * If the header is not specified, or passed in as {@code null},
         * it will be omitted when displaying this pagination.
         * </p>
         *
         * @param header The header to set
         * @return {@code this}
         */
        PaginationBuilder<TString, TCommandSource> header(
            @Nullable TString header);

        /**
         * Sets the header of this {@link PaginationBuilder}.
         *
         * <p>
         * If the header is not specified, or passed in as {@code null},
         * it will be omitted when displaying this pagination.
         * </p>
         *
         * @param header The header to set
         * @return {@code this}
         */
        PaginationBuilder<TString, TCommandSource> header(
            @Nullable Builder<TString, TCommandSource> header);

        /**
         * Sets the footer of this {@link PaginationBuilder}.
         *
         * <p>
         * If the footer is not specified, or passed in as {@code null},
         * it will be omitted when displaying this pagination.
         * </p>
         *
         * @param footer The footer to set
         * @return {@code this}
         */
        PaginationBuilder<TString, TCommandSource> footer(
            @Nullable TString footer);

        /**
         * Sets the footer of this {@link PaginationBuilder}.
         *
         * <p>
         * If the footer is not specified, or passed in as {@code null},
         * it will be omitted when displaying this pagination.
         * </p>
         *
         * @param footer The footer to set
         * @return {@code this}
         */
        PaginationBuilder<TString, TCommandSource> footer(
            @Nullable Builder<TString, TCommandSource> footer);

        /**
         * Sets the padding of this {@link PaginationBuilder}.
         *
         * @param padding The padding to set
         * @return {@code this}
         */
        PaginationBuilder<TString, TCommandSource> padding(
            TString padding);

        /**
         * Sets the padding of this {@link PaginationBuilder}.
         *
         * @param padding The padding to set
         * @return {@code this}
         */
        PaginationBuilder<TString, TCommandSource> padding(
            Builder<TString, TCommandSource> padding);

        /**
         * Sets the maximum number of lines for this {@link PaginationBuilder}.
         *
         * @param linesPerPge The lines per page to set
         * @return {@code this}
         * @throws IllegalArgumentException If linesPerPage {@literal <} 1
         */
        PaginationBuilder<TString, TCommandSource> linesPerPage(
            int linesPerPge);

        /**
         * Creates a {@link Pagination} from this builder
         *
         * @return The built {@link Pagination}
         */
        Pagination<TString, TCommandSource> build();
    }

    interface Pagination<TString, TCommandSource> {

        /**
         * @return The contents of this {@link Pagination}
         */
        Iterable<TString> getContents();

        /**
         * @return The title of this {@link Pagination}
         */
        Optional<TString> getTitle();

        /**
         * @return The header of this {@link Pagination}
         */
        Optional<TString> getHeader();

        /**
         * @return The footer of this {@link Pagination}
         */
        Optional<TString> getFooter();

        /**
         * @return The padding of this {@link Pagination}
         */
        TString getPadding();

        /**
         * @return The lines per page of this {@link Pagination}
         */
        int getLinesPerPage();

        /**
         * Sends this {@link Pagination} to the provided {@link TCommandSource receiver}
         *
         * @param receiver The {@link TCommandSource receiver} to send this {@link Pagination} to
         */
        void sendTo(TCommandSource receiver);

        /**
         * Sends this {@link Pagination} to the console
         */
        void sendToConsole();
    }
}
