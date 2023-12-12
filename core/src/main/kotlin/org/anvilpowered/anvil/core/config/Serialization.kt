package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeToken
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

fun <T : Any> Registry.serializeDefault(key: Key<T>, json: Json = Json): String = key.serialize(getDefault(key), json)

fun <T : Any> Registry.serializeStrict(key: SimpleKey<T>, json: Json = Json): String? =
    getStrict(key)?.let { key.serialize(it, json) }

fun <T : Any> Registry.serialize(key: SimpleKey<T>, json: Json = Json): String = key.serialize(get(key), json)

fun <E : Any> Registry.serializeStrict(key: ListKey<E>, json: Json = Json): String? =
    getStrict(key)?.let { key.serialize(it, json) }

fun <E : Any> Registry.serialize(key: ListKey<E>, json: Json = Json): String = key.serialize(get(key), json)

fun <K : Any, V : Any> Registry.serializeStrict(key: MapKey<K, V>, json: Json = Json): String? =
    getStrict(key)?.let { key.serialize(it, json) }

fun <K : Any, V : Any> Registry.serialize(key: MapKey<K, V>, json: Json = Json): String = key.serialize(get(key), json)

fun Registry.serialize(key: Key<*>, json: Json = Json): String = when (key) {
    is SimpleKey<*> -> serialize(key, json)
    is ListKey<*> -> serialize(key, json)
    is MapKey<*, *> -> serialize(key, json)
    else -> throw IllegalArgumentException("Unknown key type: ${key::class.simpleName}")
}

private object StringSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AnvilString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        val composite = decoder.beginStructure(descriptor)
        val result = composite.decodeStringElement(descriptor, index = 0)
        composite.endStructure(descriptor)
        return result
    }

    override fun serialize(encoder: Encoder, value: String) {
        val composite = encoder.beginStructure(descriptor)
        composite.encodeStringElement(descriptor, index = 0, value)
        composite.endStructure(descriptor)
    }
}

fun <T> TypeToken<T>.getDefaultSerializer(): KSerializer<T> {
    @Suppress("UNCHECKED_CAST")
    println("getDefaultSerializer: $type")
    return when (type) {
        String::class.java -> {
            println("getDefaultSerializer: String")
            StringSerializer as KSerializer<T>
        }
        else -> serializer(type) as KSerializer<T>
    }
}
