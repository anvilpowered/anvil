package org.anvilpowered.anvil.api.registry

import org.anvilpowered.anvil.api.registry.key.Key

/**
 * Applies the provided transformation to this registry's
 * value for the provided [Key]
 *
 * @param <T>         The value type of the provided [Key]
 * @param key         The [Key] to transform the value for
 * @param transformer The transformation to apply
 */
fun <T : Any> MutableRegistry.transform(key: Key<T>, transformer: (Key<T>, T?) -> T?): Boolean =
    set(key, transformer(key, get(key)))

/**
 * Applies the provided transformation to this registry's
 * value for the provided [Key]
 *
 * @param <T>         The value type of the provided [Key]
 * @param key         The [Key] to transform the value for
 * @param transformer The transformation to apply
 */
fun <T : Any> MutableRegistry.transform(key: Key<T>, transformer: (T?) -> T?): Boolean =
    set(key, transformer(get(key)))
