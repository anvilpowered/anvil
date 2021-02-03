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

package org.anvilpowered.anvil.api.model;

import com.google.common.collect.ImmutableList;
import jetbrains.exodus.util.ByteArraySizedInputStream;
import jetbrains.exodus.util.LightByteArrayOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Mappable<T> {

    /**
     * Writes all data from this object to the provided {@code object}.
     * <p>
     * Fields that are {@code null} in this object but not {@code null}
     * in the target object should not be overwritten.
     * </p>
     *
     * @param object Object to write data to
     * @return object with data written to. Same instance as provided object
     */
    T writeTo(T object);

    /**
     * Reads all data from the provided {@code object} to this object
     * <p>
     * Fields that are {@code null} in the provided object but not {@code null}
     * this object should not be overwritten.
     * </p>
     *
     * @param object Object to read data from
     */
    void readFrom(T object);

    static byte[] serializeUnsafe(Object object) {
        try (
            ByteArrayOutputStream baos = new LightByteArrayOutputStream();
            ObjectOutput oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not serialize object", e);
        }
    }

    static Optional<byte[]> serialize(Object object) {
        try {
            return Optional.of(serializeUnsafe(object));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T deserializeUnsafe(InputStream inputStream) {
        try (
            ObjectInput objectInputStream = new ObjectInputStream(inputStream)
        ) {
            return (T) objectInputStream.readObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not deserialize object", e);
        }
    }

    static <T> Optional<T> deserialize(InputStream inputStream) {
        try {
            return Optional.of(deserializeUnsafe(inputStream));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    static <T> T deserializeUnsafe(byte[] bytes) {
        return deserializeUnsafe(new ByteArraySizedInputStream(bytes));
    }

    static <T> Optional<T> deserialize(byte[] bytes) {
        return deserialize(new ByteArraySizedInputStream(bytes));
    }

    static <T> boolean addToCollection(InputStream inputStream, Consumer<InputStream> callback, Collection<T> elements) {
        Collection<T> collection;
        try {
            collection = deserializeUnsafe(inputStream);
        } catch (Exception ignored) {
            return false;
        }
        try {
            collection.addAll(elements);
        } catch (UnsupportedOperationException ignored) {
            Collection<T> temp = new ArrayList<>(collection.size() + elements.size());
            temp.addAll(collection);
            temp.addAll(elements);
            collection = temp;
        }
        byte[] data;
        try {
            data = serializeUnsafe(collection);
        } catch (Exception ignored) {
            return false;
        }
        callback.accept(new ByteArraySizedInputStream(data));
        return true;
    }

    @SafeVarargs
    static <T> boolean addToCollection(InputStream inputStream, Consumer<InputStream> callback, T... elements) {
        return addToCollection(inputStream, callback, ImmutableList.copyOf(elements));
    }

    static <T> boolean removeFromCollection(InputStream inputStream, Consumer<InputStream> callback,
                                            Predicate<? super T> filter) {
        Collection<T> collection;
        try {
            collection = deserializeUnsafe(inputStream);
        } catch (Exception ignored) {
            return false;
        }
        try {
            collection.removeIf(filter);
        } catch (UnsupportedOperationException ignored) {
            collection = collection.stream().filter(filter.negate()).collect(Collectors.toList());
        }
        byte[] data;
        try {
            data = serializeUnsafe(collection);
        } catch (Exception ignored) {
            return false;
        }
        callback.accept(new ByteArraySizedInputStream(data));
        return true;
    }
}
