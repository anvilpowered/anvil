package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeToken

internal class SimpleKeyBuilder<T : Any>(
    type: TypeToken<T>,
) : AbstractKeyBuilder<T, SimpleKey<T>, SimpleKey.FacetedBuilder<T>, SimpleKey.AnonymousBuilderFacet<T>, SimpleKey.NamedBuilderFacet<T>>(
    type,
), SimpleKey.FacetedBuilder<T> {

    private var serializer: ((T) -> String)? = null
    private var deserializer: ((String) -> T)? = null

    override fun self(): SimpleKey.FacetedBuilder<T> = this

    override fun serializer(serializer: ((T) -> String)?): SimpleKey.FacetedBuilder<T> {
        this.serializer = serializer
        return self()
    }

    override fun deserializer(deserializer: ((String) -> T)?): SimpleKey.FacetedBuilder<T> {
        this.deserializer = deserializer
        return self()
    }

    context(KeyNamespace)
    override fun build(): SimpleKey<T> = SimpleKey(
        type,
        requireNotNull(name) { "Name is null" },
        requireNotNull(fallback) { "Fallback is null" },
        description,
        serializer,
        deserializer ?: Key.getDefaultDeserializer(type),
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

            override fun serializer(serializer: ((T) -> String)?): SimpleKey.AnonymousBuilderFacet<T> {
                this@SimpleKeyBuilder.serializer(serializer)
                return this
            }

            override fun deserializer(deserializer: ((String) -> T)?): SimpleKey.AnonymousBuilderFacet<T> {
                this@SimpleKeyBuilder.deserializer(deserializer)
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

            override fun serializer(serializer: ((T) -> String)?): SimpleKey.NamedBuilderFacet<T> {
                this@SimpleKeyBuilder.serializer(serializer)
                return this
            }

            override fun deserializer(deserializer: ((String) -> T)?): SimpleKey.NamedBuilderFacet<T> {
                this@SimpleKeyBuilder.deserializer(deserializer)
                return this
            }
        }
    }
}
