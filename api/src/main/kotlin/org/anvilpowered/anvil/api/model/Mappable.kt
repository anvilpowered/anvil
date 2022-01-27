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
package org.anvilpowered.anvil.api.model

import com.google.common.collect.ImmutableList
import jetbrains.exodus.util.ByteArraySizedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Collectors

interface Mappable<T> {
    /**
     * Writes all data from this object to the provided `object`.
     *
     *
     * Fields that are `null` in this object but not `null`
     * in the target object should not be overwritten.
     *
     *
     * @param obj Object to write data to
     * @return object with data written to. Same instance as provided object
     */
    fun writeTo(obj: T): T

    /**
     * Reads all data from the provided `object` to this object
     *
     *
     * Fields that are `null` in the provided object but not `null`
     * this object should not be overwritten.
     *
     *
     * @param obj Object to read data from
     */
    fun readFrom(obj: T)

    companion object {
        @Throws(IOException::class)
        fun serializeUnsafe(`object`: Any?): ByteArray {
            ByteArrayOutputStream().use { baos ->
                ObjectOutputStream(baos).use { oos ->
                    oos.writeObject(`object`)
                    return baos.toByteArray()
                }
            }
        }

        fun serialize(obj: Any?): ByteArray? {
            return try {
                serializeUnsafe(obj)
            } catch (ignored: IOException) {
                null
            }
        }

        @Throws(IOException::class, ClassNotFoundException::class)
        fun <T> deserializeUnsafe(inputStream: InputStream?): T {
            ObjectInputStream(inputStream).use { objectInputStream -> return objectInputStream.readObject() as T }
        }

        fun <T> deserialize(inputStream: InputStream?): T? {
            return try {
                deserializeUnsafe(inputStream)
            } catch (ignored: IOException) {
                null
            } catch (ignored: ClassNotFoundException) {
                null
            } catch (ignored: ClassCastException) {
                null
            }
        }

        fun <T> addToCollection(inputStream: InputStream?, callback: Consumer<InputStream?>, elements: Collection<T>): Boolean {
            var collection: MutableCollection<T>
            collection = try {
                deserializeUnsafe(inputStream)
            } catch (ignored: IOException) {
                return false
            } catch (ignored: ClassNotFoundException) {
                return false
            }
            try {
                collection.addAll(elements)
            } catch (ignored: UnsupportedOperationException) {
                val temp: MutableCollection<T> = ArrayList(collection.size + elements.size)
                temp.addAll(collection)
                temp.addAll(elements)
                collection = temp
            }
            val data: ByteArray = try {
                serializeUnsafe(collection)
            } catch (ignored: IOException) {
                return false
            }
            callback.accept(ByteArraySizedInputStream(data))
            return true
        }

        @SafeVarargs
        fun <T> addToCollection(inputStream: InputStream?, callback: Consumer<InputStream?>, vararg elements: T): Boolean {
            return addToCollection(inputStream, callback, ImmutableList.copyOf(elements))
        }

        fun <T> removeFromCollection(
            inputStream: InputStream?, callback: Consumer<InputStream?>,
            filter: Predicate<in T>,
        ): Boolean {
            var collection: MutableCollection<T>
            collection = try {
                deserializeUnsafe(inputStream)
            } catch (ignored: IOException) {
                return false
            } catch (ignored: ClassNotFoundException) {
                return false
            }
            try {
                collection.removeIf(filter)
            } catch (ignored: UnsupportedOperationException) {
                collection = collection.stream().filter(filter.negate()).collect(Collectors.toList())
            }
            val data: ByteArray = try {
                serializeUnsafe(collection)
            } catch (ignored: IOException) {
                return false
            }
            callback.accept(ByteArraySizedInputStream(data))
            return true
        }
    }
}
