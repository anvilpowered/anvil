/*
 *   Anvil - AnvilPowered
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

package org.anvilpowered.anvil.api.data.key;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("UnstableApiUsage")
public abstract class Key<T> extends TypeToken<T> implements Comparable<Key<T>> {

    private final String name;
    private final T fallbackValue;

    protected Key(String name, T fallbackValue) {
        this.name = name;
        this.fallbackValue = fallbackValue;
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

    public String getName() {
        return name;
    }

    public T getFallbackValue() {
        return fallbackValue;
    }
}
