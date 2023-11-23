package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeFactory
import io.leangen.geantyref.TypeToken

class MapKeyBuilder<K : Any, V : Any>(
    private val mapKeyType: TypeToken<K>,
    private val mapValueType: TypeToken<V>,
) : AbstractKeyBuilder<
    Map<K, V>, MapKey<K, V>, MapKey.FacetedBuilder<K, V>, MapKey.AnonymousBuilderFacet<K, V>,
    MapKey.NamedBuilderFacet<K, V>,
    >(createMapTypeToken(mapKeyType, mapValueType)), MapKey.FacetedBuilder<K, V> {

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
        mapKeyType,
        keySerializer,
        keyDeserializer ?: Key.getDefaultDeserializer(mapKeyType),
        mapValueType,
        valueSerializer,
        valueDeserializer ?: Key.getDefaultDeserializer(mapValueType),
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

@Suppress("UNCHECKED_CAST")
private fun <K : Any, V : Any> createMapTypeToken(mapKeyType: TypeToken<K>, mapValueType: TypeToken<V>): TypeToken<Map<K, V>> =
    TypeToken.get(TypeFactory.parameterizedClass(Map::class.java, mapKeyType.type, mapValueType.type)) as TypeToken<Map<K, V>>
