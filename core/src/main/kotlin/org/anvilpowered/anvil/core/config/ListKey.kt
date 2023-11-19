package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeToken

context(KeyNamespace)
class ListKey<E : Any>(
    override val type: TypeToken<List<E>>,
    override val name: String,
    override val fallback: List<E>,
    override val description: String?,
    private val elementType: TypeToken<E>,
    private val elementSerializer: (E) -> String,
    private val elementDeserializer: (String) -> E?,
) : Key<List<E>> {
    private val namespace: KeyNamespace = this@KeyNamespace

    init {
        namespace.add(this)
    }

    fun serializeElement(element: E): String = elementSerializer(element)
    fun deserializeElement(element: String): E? = elementDeserializer(element)

    override fun compareTo(other: Key<List<E>>): Int = Key.comparator.compare(this, other)
    override fun equals(other: Any?): Boolean = (other as Key<*>?)?.let { Key.equals(this, it) } ?: false
    override fun hashCode(): Int = Key.hashCode(this)
    override fun toString(): String = "ListKey<$elementType>(name='$name')"

    @KeyBuilderDsl
    interface BuilderFacet<E : Any, B : BuilderFacet<E, B>> : Key.BuilderFacet<List<E>, ListKey<E>, B> {

        /**
         * Sets the element serializer of the generated [Key].
         *
         * @param serializer The element serializer to set or `null` to remove it
         * @return `this`
         */
        @KeyBuilderDsl
        fun elementSerializer(serializer: ((E) -> String)?): B

        /**
         * Sets the element deserializer of the generated [Key].
         *
         * @param deserializer The element deserializer to set or `null` to remove it
         * @return `this`
         */
        @KeyBuilderDsl
        fun elementDeserializer(deserializer: ((String) -> E)?): B
    }

    @KeyBuilderDsl
    interface AnonymousBuilderFacet<E : Any> : BuilderFacet<E, AnonymousBuilderFacet<E>>,
        Key.BuilderFacet<List<E>, ListKey<E>, AnonymousBuilderFacet<E>>

    @KeyBuilderDsl
    interface NamedBuilderFacet<E : Any> : BuilderFacet<E, NamedBuilderFacet<E>>,
        Key.NamedBuilderFacet<List<E>, ListKey<E>, NamedBuilderFacet<E>>

    @KeyBuilderDsl
    interface Builder<E : Any> : BuilderFacet<E, Builder<E>>,
        Key.Builder<List<E>, ListKey<E>, Builder<E>>
}
