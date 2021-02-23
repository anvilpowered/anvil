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

package org.anvilpowered.anvil.api.registry;

import com.google.common.base.Preconditions;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
class KeyBuilder<T> implements Key.Builder<T> {

    private final TypeToken<T> type;
    @MonotonicNonNull
    private String name;
    @Nullable
    private T fallbackValue;
    private boolean userImmutable;
    private boolean sensitive;
    @Nullable
    private String description;
    @Nullable
    private Function<String, T> parser;
    @Nullable
    private Function<T, String> toStringer;

    KeyBuilder(TypeToken<T> type) {
        this.type = Preconditions.checkNotNull(type, "type");
        this.sensitive = false;
    }

    @Override
    public Key.Builder<T> name(String name) {
        this.name = Preconditions.checkNotNull(name, "name");
        return this;
    }

    @Override
    public Key.Builder<T> fallback(@Nullable T fallbackValue) {
        this.fallbackValue = fallbackValue;
        return this;
    }

    @Override
    public KeyBuilder<T> userImmutable() {
        userImmutable = true;
        return this;
    }

    @Override
    public KeyBuilder<T> sensitive() {
        sensitive = true;
        return this;
    }

    @Override
    public KeyBuilder<T> description(@Nullable String description) {
        this.description = description;
        return this;
    }

    @Override
    public KeyBuilder<T> parser(@Nullable Function<String, T> parser) {
        this.parser = parser;
        return this;
    }

    @Override
    public KeyBuilder<T> toStringer(@Nullable Function<T, String> toStringer) {
        this.toStringer = toStringer;
        return this;
    }

    @Override
    public Key<T> build() {
        Preconditions.checkNotNull(name, "name");
        return new Key<T>(type, name, fallbackValue, userImmutable, sensitive, description, parser, toStringer) {
        };
    }
}
