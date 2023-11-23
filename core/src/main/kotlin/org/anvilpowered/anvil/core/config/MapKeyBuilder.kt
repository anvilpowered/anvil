package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeToken

internal class MapKeyBuilder<K : Any, V : Any>(
    type: TypeToken<Map<K, V>>,
    private val keyType: TypeToken<K>,
    private val valueType: TypeToken<V>,
) : AbstractKeyBuilder<
    Map<K, V>, MapKey<K, V>, MapKey.FacetedBuilder<K, V>, MapKey.AnonymousBuilderFacet<K, V>,
    MapKey.NamedBuilderFacet<K, V>,
    >(type), MapKey.FacetedBuilder<K, V> {

    private var keySerializer: ((K) -> String)? = null
    private var keyDeserializer: ((String) -> K)? = null
    private var valueSerializer: ((V) -> String)? = null
    private var valueDeserializer: ((String) -> V)? = null

    override fun self(): MapKey.FacetedBuilder<K, V> = this

    override fun keySerializer(serializer: ((K) -> String)?): MapKey.FacetedBuilder<K, V> {
        this.keySerializer = serializer
        return this
    }

    override fun keyDeserializer(deserializer: ((String) -> K)?): MapKey.FacetedBuilder<K, V> {
        this.keyDeserializer = deserializer
        return this
    }

    override fun valueSerializer(serializer: ((V) -> String)?): MapKey.FacetedBuilder<K, V> {
        this.valueSerializer = serializer
        return this
    }

    override fun valueDeserializer(deserializer: ((String) -> V)?): MapKey.FacetedBuilder<K, V> {
        this.valueDeserializer = deserializer
        return this
    }

    context(KeyNamespace)
    override fun build(): MapKey<K, V> = MapKey(
        type,
        requireNotNull(name) { "Name is null" },
        requireNotNull(fallback) { "Fallback is null" },
        description,
        keyType,
        keySerializer,
        keyDeserializer ?: Key.getDefaultDeserializer(keyType),
        valueType,
        valueSerializer,
        valueDeserializer ?: Key.getDefaultDeserializer(valueType),
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

            override fun keySerializer(serializer: ((K) -> String)?): MapKey.AnonymousBuilderFacet<K, V> {
                this@MapKeyBuilder.keySerializer(serializer)
                return this
            }

            override fun keyDeserializer(deserializer: ((String) -> K)?): MapKey.AnonymousBuilderFacet<K, V> {
                this@MapKeyBuilder.keyDeserializer(deserializer)
                return this
            }

            override fun valueSerializer(serializer: ((V) -> String)?): MapKey.AnonymousBuilderFacet<K, V> {
                this@MapKeyBuilder.valueSerializer(serializer)
                return this
            }

            override fun valueDeserializer(deserializer: ((String) -> V)?): MapKey.AnonymousBuilderFacet<K, V> {
                this@MapKeyBuilder.valueDeserializer(deserializer)
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

            override fun keySerializer(serializer: ((K) -> String)?): MapKey.NamedBuilderFacet<K, V> {
                this@MapKeyBuilder.keySerializer(serializer)
                return this
            }

            override fun keyDeserializer(deserializer: ((String) -> K)?): MapKey.NamedBuilderFacet<K, V> {
                this@MapKeyBuilder.keyDeserializer(deserializer)
                return this
            }

            override fun valueSerializer(serializer: ((V) -> String)?): MapKey.NamedBuilderFacet<K, V> {
                this@MapKeyBuilder.valueSerializer(serializer)
                return this
            }

            override fun valueDeserializer(deserializer: ((String) -> V)?): MapKey.NamedBuilderFacet<K, V> {
                this@MapKeyBuilder.valueDeserializer(deserializer)
                return this
            }
        }
    }
}
