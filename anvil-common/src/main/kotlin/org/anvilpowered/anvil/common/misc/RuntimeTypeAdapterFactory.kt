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

package org.anvilpowered.anvil.common.misc

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * Borrowed from com.google.gson.typeadapters with modifications
 */
class RuntimeTypeAdapterFactory<T>(
  private val baseType: Class<T>,
  private val typeFieldName: String = "className",
) : TypeAdapterFactory {

  fun <R : Any> Gson.getDelegateFor(type: Class<R>): TypeAdapter<R> {
    return getDelegateAdapter(this@RuntimeTypeAdapterFactory, TypeToken.get(type))
  }

  fun <R : Any> Gson.getDelegateFor(typeName: String): TypeAdapter<R> {
    val type = try {
      Class.forName(typeName) as Class<R>
    } catch (e: ClassNotFoundException) {
      throw IllegalStateException("Could not find type $typeName", e)
    }
    return getDelegateFor(type)
  }

  override fun <R : Any> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
    if (!baseType.isAssignableFrom(type.rawType)) {
      return null
    }
    return object : TypeAdapter<R>() {
      @Throws(IOException::class)
      override fun read(`in`: JsonReader): R {
        val jsonElement = Streams.parse(`in`)
        val labelJsonElement: JsonElement = jsonElement.asJsonObject[typeFieldName]
          ?: throw JsonParseException("cannot deserialize $baseType because it does not define a field named $typeFieldName")
        return gson.getDelegateFor<R>(labelJsonElement.asString).fromJsonTree(jsonElement)
      }

      @Throws(IOException::class)
      override fun write(out: JsonWriter, value: R) {
        val srcType: Class<R> = value::class.java as Class<R>
        val delegate = gson.getDelegateFor(srcType)
        val jsonObject = delegate.toJsonTree(value).asJsonObject
        if (jsonObject.has(typeFieldName)) {
          throw JsonParseException("cannot serialize ${srcType.name} because it already defines a field named $typeFieldName")
        }
        jsonObject.add(typeFieldName, JsonPrimitive(srcType.name))
        Streams.write(jsonObject, out)
      }
    }.nullSafe()
  }
}
