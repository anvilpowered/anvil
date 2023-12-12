package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeToken
import kotlinx.serialization.KSerializer
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

fun <T> TypeToken<T>.getDefaultSerializer(): KSerializer<T> {
    @Suppress("UNCHECKED_CAST")
    return serializer(type) as KSerializer<T>
}

internal fun <T> String.prepareForDecode(targetType: TypeToken<T>): String {
    // We use the JSON deserializer for all values
    // It expects input String to be quoted when the target type is a String
    return when (targetType.type) {
        String::class.java -> "\"$this\""
        else -> this
    }
}
