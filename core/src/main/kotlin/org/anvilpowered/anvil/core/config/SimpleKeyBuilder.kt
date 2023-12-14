/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
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

import io.leangen.geantyref.TypeToken
import kotlinx.serialization.KSerializer

class SimpleKeyBuilder<T : Any>(
    type: TypeToken<T>,
) : AbstractKeyBuilder<T, SimpleKey<T>, SimpleKey.FacetedBuilder<T>, SimpleKey.AnonymousBuilderFacet<T>, SimpleKey.NamedBuilderFacet<T>>(
    type,
),
    SimpleKey.FacetedBuilder<T> {

    private var serializer: KSerializer<T>? = null

    override fun self(): SimpleKey.FacetedBuilder<T> = this

    override fun serializer(serializer: KSerializer<T>?): SimpleKey.FacetedBuilder<T> {
        this.serializer = serializer
        return self()
    }

    context(KeyNamespace)
    override fun build(): SimpleKey<T> = SimpleKey(
        type,
        requireNotNull(name) { "Name is null" },
        requireNotNull(fallback) { "Fallback is null" },
        description,
        serializer ?: type.getDefaultSerializer(),
    )

    override fun asAnonymousFacet(): SimpleKey.AnonymousBuilderFacet<T> {
        return object : SimpleKey.AnonymousBuilderFacet<T> {
            override fun fallback(fallback: T?): SimpleKey.AnonymousBuilderFacet<T> {
                this@SimpleKeyBuilder.fallback(fallback).let { this }
                return this
            }

            override fun description(description: String?): SimpleKey.AnonymousBuilderFacet<T> {
                this@SimpleKeyBuilder.description(description)
                return this
            }

            override fun serializer(serializer: KSerializer<T>?): SimpleKey.AnonymousBuilderFacet<T> {
                this@SimpleKeyBuilder.serializer(serializer)
                return this
            }
        }
    }

    override fun asNamedFacet(): SimpleKey.NamedBuilderFacet<T> {
        return object : SimpleKey.NamedBuilderFacet<T> {
            override fun name(name: String): SimpleKey.NamedBuilderFacet<T> {
                this@SimpleKeyBuilder.name(name)
                return this
            }

            override fun fallback(fallback: T?): SimpleKey.NamedBuilderFacet<T> {
                this@SimpleKeyBuilder.fallback(fallback)
                return this
            }

            override fun description(description: String?): SimpleKey.NamedBuilderFacet<T> {
                this@SimpleKeyBuilder.description(description)
                return this
            }

            override fun serializer(serializer: KSerializer<T>?): SimpleKey.NamedBuilderFacet<T> {
                this@SimpleKeyBuilder.serializer(serializer)
                return this
            }
        }
    }
}
