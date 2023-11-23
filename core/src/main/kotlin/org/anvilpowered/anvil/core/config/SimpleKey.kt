package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeToken

context(KeyNamespace)
class SimpleKey<T : Any> internal constructor(
    override val type: TypeToken<T>,
    override val name: String,
    override val fallback: T,
    override val description: String?,
    private val serializer: ((T) -> String)?,
    private val deserializer: (String) -> T,
) : Key<T> {
    private val namespace: KeyNamespace = this@KeyNamespace

    init {
        namespace.add(this)
    }

    override fun serialize(value: T): String = serializer?.invoke(value) ?: value.toString()
    override fun deserialize(value: String): T = deserializer(value)

    override fun compareTo(other: Key<T>): Int = Key.comparator.compare(this, other)
    override fun equals(other: Any?): Boolean = (other as Key<*>?)?.let { Key.equals(this, it) } ?: false
    override fun hashCode(): Int = Key.hashCode(this)
    override fun toString(): String = "SimpleKey(type=$type, name='$name')"

    @KeyBuilderDsl
    interface BuilderFacet<T : Any, B : BuilderFacet<T, B>> : Key.BuilderFacet<T, SimpleKey<T>, B> {

        /**
         * Sets the serializer of the generated [Key].
         *
         * @param serializer The serializer to set or `null` to remove it
         * @return `this`
         */
        @KeyBuilderDsl
        fun serializer(serializer: ((T) -> String)?): B

        /**
         * Sets the deserializer of the generated [Key].
         *
         * @param deserializer The deserializer to set or `null` to remove it
         * @return `this`
         */
        @KeyBuilderDsl
        fun deserializer(deserializer: ((String) -> T)?): B
    }

    @KeyBuilderDsl
    interface AnonymousBuilderFacet<T : Any> : BuilderFacet<T, AnonymousBuilderFacet<T>>,
        Key.BuilderFacet<T, SimpleKey<T>, AnonymousBuilderFacet<T>>

    @KeyBuilderDsl
    interface NamedBuilderFacet<T : Any> : BuilderFacet<T, NamedBuilderFacet<T>>,
        Key.NamedBuilderFacet<T, SimpleKey<T>, NamedBuilderFacet<T>>

    @KeyBuilderDsl
    interface Builder<T : Any> : BuilderFacet<T, Builder<T>>,
        Key.Builder<T, SimpleKey<T>, Builder<T>>

    @KeyBuilderDsl
    interface FacetedBuilder<T : Any> : BuilderFacet<T, FacetedBuilder<T>>,
        Key.FacetedBuilder<T, SimpleKey<T>, FacetedBuilder<T>, AnonymousBuilderFacet<T>, NamedBuilderFacet<T>>
}
