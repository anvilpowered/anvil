package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeToken

internal class ListKeyBuilder<E : Any>(
    type: TypeToken<List<E>>,
    private val elementType: TypeToken<E>,
) : AbstractKeyBuilder<List<E>, ListKey<E>, ListKey.FacetedBuilder<E>, ListKey.AnonymousBuilderFacet<E>, ListKey.NamedBuilderFacet<E>>(type),
    ListKey.FacetedBuilder<E> {

    private var elementSerializer: ((E) -> String)? = null
    private var elementDeserializer: ((String) -> E)? = null

    override fun self(): ListKey.FacetedBuilder<E> = this

    override fun elementSerializer(serializer: ((E) -> String)?): ListKey.FacetedBuilder<E> {
        this.elementSerializer = serializer
        return this
    }

    override fun elementDeserializer(deserializer: ((String) -> E)?): ListKey.FacetedBuilder<E> {
        this.elementDeserializer = deserializer
        return this
    }

    context(KeyNamespace)
    override fun build(): ListKey<E> = ListKey(
        type,
        requireNotNull(name) { "Name is null" },
        requireNotNull(fallback) { "Fallback is null" },
        description,
        elementType,
        requireNotNull(elementSerializer) { "Element serializer is null" },
        requireNotNull(elementDeserializer) { "Element deserializer is null" },
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

            override fun elementSerializer(serializer: ((E) -> String)?): ListKey.AnonymousBuilderFacet<E> {
                this@ListKeyBuilder.elementSerializer(serializer)
                return this
            }

            override fun elementDeserializer(deserializer: ((String) -> E)?): ListKey.AnonymousBuilderFacet<E> {
                this@ListKeyBuilder.elementDeserializer(deserializer)
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

            override fun elementSerializer(serializer: ((E) -> String)?): ListKey.NamedBuilderFacet<E> {
                this@ListKeyBuilder.elementSerializer(serializer)
                return this
            }

            override fun elementDeserializer(deserializer: ((String) -> E)?): ListKey.NamedBuilderFacet<E> {
                this@ListKeyBuilder.elementDeserializer(deserializer)
                return this
            }
        }
    }
}
