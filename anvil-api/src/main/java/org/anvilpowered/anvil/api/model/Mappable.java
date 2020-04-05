/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.api.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Optional;

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

    static byte[] serializeUnsafe(Object object) throws IOException {
        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(object);
            return baos.toByteArray();
        }
    }

    static Optional<byte[]> serialize(Object object) {
        try {
            return Optional.of(serializeUnsafe(object));
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T deserializeUnsafe(InputStream inputStream) throws IOException, ClassNotFoundException {
        try (
            ObjectInput objectInputStream = new ObjectInputStream(inputStream)
        ) {
            return (T) objectInputStream.readObject();
        }
    }

    static <T> Optional<T> deserialize(InputStream inputStream) {
        try {
            return Optional.of(deserializeUnsafe(inputStream));
        } catch (IOException | ClassNotFoundException | ClassCastException ignored) {
            return Optional.empty();
        }
    }
}
