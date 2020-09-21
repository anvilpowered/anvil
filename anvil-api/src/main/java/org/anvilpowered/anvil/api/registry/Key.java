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

package org.anvilpowered.anvil.api.registry;

import com.google.common.reflect.TypeToken;
import org.anvilpowered.anvil.api.misc.Named;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Function;

@SuppressWarnings({"UnstableApiUsage", "unchecked"})
public abstract class Key<T> extends TypeToken<T> implements Named, Comparable<Key<T>> {

    private final String name;
    private final T fallbackValue;
    private final boolean userImmutable;
    private final boolean sensitive;
    @Nullable
    private final String description;
    @Nullable
    private final Function<String, T> parser;
    @Nullable
    private final Function<T, String> toStringer;

    @Deprecated // to be removed in 0.4, use Key.of(String, T) instead
    protected Key(String name, T fallbackValue) {
        this(Key.class, name, fallbackValue, false, false, null, null, null);
    }

    Key(
        Class<?> declaringClass,
        String name,
        T fallbackValue,
        boolean userImmutable,
        boolean sensitive,
        @Nullable String description,
        @Nullable Function<String, T> parser,
        @Nullable Function<T, String> toStringer
    ) {
        super(declaringClass);
        this.name = name;
        this.fallbackValue = fallbackValue;
        this.userImmutable = userImmutable;
        this.sensitive = sensitive;
        this.description = description;
        if (parser == null) {
            this.parser = extractParser(fallbackValue);
        } else {
            this.parser = parser;
        }
        this.toStringer = toStringer;
    }

    interface Builder<T> {

        /**
         * Indicates that the generated {@link Key} cannot be changed by the user.
         *
         * @return {@code this}
         */
        Builder<T> userImmutable();

        /**
         * Indicates that the generated {@link Key} is sensitive (e.g. connection details) that should not
         * be accessible through regedit by default. Values of sensitive keys can only be viewed or modified
         * through registries that have {@link Keys#REGEDIT_ALLOW_SENSITIVE} enabled.
         *
         * @return {@code this}
         */
        Builder<T> sensitive();

        /**
         * Sets the description of the generated {@link Key}.
         *
         * @param description The description to set or {@code null} to remove it
         * @return {@code this}
         */
        Builder<T> description(@Nullable String description);

        /**
         * Sets the parser of the generated {@link Key}.
         *
         * @param parser The parser to set or {@code null} to remove it
         * @return {@code this}
         */
        Builder<T> parser(@Nullable Function<String, T> parser);

        /**
         * Sets the toStringer of the generated {@link Key}.
         *
         * @param toStringer The toStringer to set or {@code null} to remove it
         * @return {@code this}
         */
        Builder<T> toStringer(@Nullable Function<T, String> toStringer);

        /**
         * Generates a {@link Key} based on this builder.
         *
         * @return The generated {@link Key}
         */
        Key<T> build();
    }

    public static <T> Builder<T> builder(String name, T fallbackValue) {
        return new KeyBuilder<>(name, fallbackValue);
    }

    @Nullable
    private Function<String, T> extractParser(T value) {
        if (value instanceof String) {
            return s -> (T) s;
        } else if (value instanceof Boolean) {
            return s -> (T) Boolean.valueOf(s);
        } else if (value instanceof Double) {
            return s -> (T) Double.valueOf(s);
        } else if (value instanceof Float) {
            return s -> (T) Float.valueOf(s);
        } else if (value instanceof Long) {
            return s -> (T) Long.valueOf(s);
        } else if (value instanceof Integer) {
            return s -> (T) Integer.valueOf(s);
        } else if (value instanceof Short) {
            return s -> (T) Short.valueOf(s);
        } else if (value instanceof Byte) {
            return s -> (T) Byte.valueOf(s);
        }
        return null;
    }

    @Nullable
    public T parse(String value) {
        if (parser == null) {
            return null;
        }
        return parser.apply(value);
    }

    @Nullable
    public String toString(T value) {
        if (toStringer == null) {
            return value.toString();
        }
        return toStringer.apply(value);
    }

    @Override
    public int compareTo(Key<T> o) {
        return name.compareToIgnoreCase(o.name);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Key && name.equalsIgnoreCase(((Key<?>) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    public T getFallbackValue() {
        return fallbackValue;
    }

    public boolean isUserImmutable() {
        return userImmutable;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    @Nullable
    public String getDescription() {
        return description;
    }
}
