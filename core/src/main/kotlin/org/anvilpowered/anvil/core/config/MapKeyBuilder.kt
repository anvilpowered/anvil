/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2026 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeFactory
import io.leangen.geantyref.TypeToken
import kotlinx.serialization.KSerializer

class MapKeyBuilder<K : Any, V : Any>(
  private val mapKeyType: TypeToken<K>,
  private val mapValueType: TypeToken<V>,
) : AbstractKeyBuilder<
  Map<K, V>, MapKey<K, V>, MapKey.FacetedBuilder<K, V>, MapKey.AnonymousBuilderFacet<K, V>,
  MapKey.NamedBuilderFacet<K, V>,
  >(createMapTypeToken(mapKeyType, mapValueType)),
  MapKey.FacetedBuilder<K, V> {

  private var keySerializer: KSerializer<K>? = null
  private var valueSerializer: KSerializer<V>? = null

  override fun self(): MapKey.FacetedBuilder<K, V> = this

  override fun keySerializer(serializer: KSerializer<K>?): MapKey.FacetedBuilder<K, V> {
    this.keySerializer = serializer
    return this
  }

  override fun valueSerializer(serializer: KSerializer<V>?): MapKey.FacetedBuilder<K, V> {
    this.valueSerializer = serializer
    return this
  }

  context(KeyNamespace)
  @Suppress("UNCHECKED_CAST")
  override fun build(): MapKey<K, V> = MapKey(
    type,
    requireNotNull(name) { "Name is null" },
    requireNotNull(fallback) { "Fallback is null" },
    description,
    mapKeyType,
    keySerializer ?: mapKeyType.getDefaultSerializer(),
    mapValueType,
    valueSerializer ?: mapValueType.getDefaultSerializer(),
  )

  override fun asAnonymousFacet(): MapKey.AnonymousBuilderFacet<K, V> {
    return object : MapKey.AnonymousBuilderFacet<K, V> {
      override fun fallback(fallback: Map<K, V>?): MapKey.AnonymousBuilderFacet<K, V> {
        this@MapKeyBuilder.fallback(fallback)
        return this
      }

      override fun description(description: String?): MapKey.AnonymousBuilderFacet<K, V> {
        this@MapKeyBuilder.description(description)
        return this
      }

      override fun keySerializer(serializer: KSerializer<K>?): MapKey.AnonymousBuilderFacet<K, V> {
        this@MapKeyBuilder.keySerializer(serializer)
        return this
      }

      override fun valueSerializer(serializer: KSerializer<V>?): MapKey.AnonymousBuilderFacet<K, V> {
        this@MapKeyBuilder.valueSerializer(serializer)
        return this
      }
    }
  }

  override fun asNamedFacet(): MapKey.NamedBuilderFacet<K, V> {
    return object : MapKey.NamedBuilderFacet<K, V> {
      override fun name(name: String): MapKey.NamedBuilderFacet<K, V> {
        this@MapKeyBuilder.name(name)
        return this
      }

      override fun fallback(fallback: Map<K, V>?): MapKey.NamedBuilderFacet<K, V> {
        this@MapKeyBuilder.fallback(fallback)
        return this
      }

      override fun description(description: String?): MapKey.NamedBuilderFacet<K, V> {
        this@MapKeyBuilder.description(description)
        return this
      }

      override fun keySerializer(serializer: KSerializer<K>?): MapKey.NamedBuilderFacet<K, V> {
        this@MapKeyBuilder.keySerializer(serializer)
        return this
      }

      override fun valueSerializer(serializer: KSerializer<V>?): MapKey.NamedBuilderFacet<K, V> {
        this@MapKeyBuilder.valueSerializer(serializer)
        return this
      }
    }
  }
}

@Suppress("UNCHECKED_CAST")
private fun <K : Any, V : Any> createMapTypeToken(mapKeyType: TypeToken<K>, mapValueType: TypeToken<V>): TypeToken<Map<K, V>> =
  TypeToken.get(TypeFactory.parameterizedClass(Map::class.java, mapKeyType.type, mapValueType.type)) as TypeToken<Map<K, V>>
