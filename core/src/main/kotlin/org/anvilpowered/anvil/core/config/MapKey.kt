package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeToken

context(KeyNamespace)
class MapKey<K : Any, V : Any> internal constructor(
    override val type: TypeToken<Map<K, V>>,
    override val name: String,
    override val fallback: Map<K, V>,
    override val description: String?,
    private val keyType: TypeToken<K>,
    private val keySerializer: ((K) -> String)?,
    private val keyDeserializer: (String) -> K,
    private val valueType: TypeToken<V>,
    private val valueSerializer: ((V) -> String)?,
    private val valueDeserializer: (String) -> V,
) : Key<Map<K, V>> {
    private val namespace: KeyNamespace = this@KeyNamespace

    init {
        namespace.add(this)
    }

    fun serializeKey(mapKey: K): String = keySerializer?.invoke(mapKey) ?: mapKey.toString()
    fun deserializeKey(mapKey: String): K = keyDeserializer(mapKey)
    fun serializeValue(mapValue: V): String = valueSerializer?.invoke(mapValue) ?: mapValue.toString()
    fun deserializeValue(mapValue: String): V = valueDeserializer(mapValue)

    override fun serialize(value: Map<K, V>): String {
        return value.entries.joinToString(",") { (key, value) ->
            "${serializeKey(key)}=${serializeValue(value)}"
        }
    }

    override fun deserialize(value: String): Map<K, V> {
        return value.splitToSequence(",")
            .map { it.split("=", limit = 2) }
            .map { (key, value) -> deserializeKey(key) to deserializeValue(value) }
            .toMap()
    }

    override fun compareTo(other: Key<Map<K, V>>): Int = Key.comparator.compare(this, other)
    override fun equals(other: Any?): Boolean = (other as Key<*>?)?.let { Key.equals(this, it) } ?: false
    override fun hashCode(): Int = Key.hashCode(this)
    override fun toString(): String = "MapKey<$keyType, $valueType>(name='$name')"

    @KeyBuilderDsl
    interface BuilderFacet<K : Any, V : Any, B : BuilderFacet<K, V, B>> : Key.BuilderFacet<Map<K, V>, MapKey<K, V>, B> {

        /**
         * Sets the key serializer of the generated [Key].
         *
         * @param serializer The key serializer to set or `null` to remove it
         * @return `this`
         */
        @KeyBuilderDsl
        fun keySerializer(serializer: ((K) -> String)?): B

        /**
         * Sets the key deserializer of the generated [Key].
         *
         * @param deserializer The key deserializer to set or `null` to remove it
         * @return `this`
         */
        @KeyBuilderDsl
        fun keyDeserializer(deserializer: ((String) -> K)?): B

        /**
         * Sets the value serializer of the generated [Key].
         *
         * @param serializer The value serializer to set or `null` to remove it
         * @return `this`
         */
        @KeyBuilderDsl
        fun valueSerializer(serializer: ((V) -> String)?): B

        /**
         * Sets the value deserializer of the generated [Key].
         *
         * @param deserializer The value deserializer to set or `null` to remove it
         * @return `this`
         */
        @KeyBuilderDsl
        fun valueDeserializer(deserializer: ((String) -> V)?): B
    }

    @KeyBuilderDsl
    interface AnonymousBuilderFacet<K : Any, V : Any> : BuilderFacet<K, V, AnonymousBuilderFacet<K, V>>,
        Key.BuilderFacet<Map<K, V>, MapKey<K, V>, AnonymousBuilderFacet<K, V>>

    @KeyBuilderDsl
    interface NamedBuilderFacet<K : Any, V : Any> : BuilderFacet<K, V, NamedBuilderFacet<K, V>>,
        Key.NamedBuilderFacet<Map<K, V>, MapKey<K, V>, NamedBuilderFacet<K, V>>

    @KeyBuilderDsl
    interface Builder<K : Any, V : Any> : BuilderFacet<K, V, Builder<K, V>>,
        Key.Builder<Map<K, V>, MapKey<K, V>, Builder<K, V>>

    @KeyBuilderDsl
    interface FacetedBuilder<K : Any, V : Any> : BuilderFacet<K, V, FacetedBuilder<K, V>>,
        Key.FacetedBuilder<Map<K, V>, MapKey<K, V>, FacetedBuilder<K, V>, AnonymousBuilderFacet<K, V>, NamedBuilderFacet<K, V>>
}
