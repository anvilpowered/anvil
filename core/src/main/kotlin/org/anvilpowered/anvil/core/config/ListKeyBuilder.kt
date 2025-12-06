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

class ListKeyBuilder<E : Any>(
  private val elementType: TypeToken<E>,
) : AbstractKeyBuilder<List<E>, ListKey<E>, ListKey.FacetedBuilder<E>, ListKey.AnonymousBuilderFacet<E>, ListKey.NamedBuilderFacet<E>>(
    createListTypeToken(elementType),
  ),
  ListKey.FacetedBuilder<E> {
  private var elementSerializer: KSerializer<E>? = null

  override fun self(): ListKey.FacetedBuilder<E> = this

  override fun elementSerializer(serializer: KSerializer<E>?): ListKey.FacetedBuilder<E> {
    this.elementSerializer = serializer
    return this
  }

  context(KeyNamespace)
  @Suppress("UNCHECKED_CAST")
  override fun build(): ListKey<E> =
    ListKey(
      type,
      requireNotNull(name) { "Name is null" },
      requireNotNull(fallback) { "Fallback is null" },
      description,
      elementType,
      elementSerializer ?: elementType.getDefaultSerializer(),
    )

  override fun asAnonymousFacet(): ListKey.AnonymousBuilderFacet<E> {
    return object : ListKey.AnonymousBuilderFacet<E> {
      override fun fallback(fallback: List<E>?): ListKey.AnonymousBuilderFacet<E> {
        this@ListKeyBuilder.fallback(fallback)
        return this
      }

      override fun description(description: String?): ListKey.AnonymousBuilderFacet<E> {
        this@ListKeyBuilder.description(description)
        return this
      }

      override fun elementSerializer(serializer: KSerializer<E>?): ListKey.AnonymousBuilderFacet<E> {
        this@ListKeyBuilder.elementSerializer(serializer)
        return this
      }
    }
  }

  override fun asNamedFacet(): ListKey.NamedBuilderFacet<E> {
    return object : ListKey.NamedBuilderFacet<E> {
      override fun fallback(fallback: List<E>?): ListKey.NamedBuilderFacet<E> {
        this@ListKeyBuilder.fallback(fallback)
        return this
      }

      override fun description(description: String?): ListKey.NamedBuilderFacet<E> {
        this@ListKeyBuilder.description(description)
        return this
      }

      override fun name(name: String): ListKey.NamedBuilderFacet<E> {
        this@ListKeyBuilder.name(name)
        return this
      }

      override fun elementSerializer(serializer: KSerializer<E>?): ListKey.NamedBuilderFacet<E> {
        this@ListKeyBuilder.elementSerializer(serializer)
        return this
      }
    }
  }
}

@Suppress("UNCHECKED_CAST")
private fun <E : Any> createListTypeToken(elementType: TypeToken<E>): TypeToken<List<E>> =
  TypeToken.get(TypeFactory.parameterizedClass(List::class.java, elementType.type)) as TypeToken<List<E>>
