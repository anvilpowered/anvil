package org.anvilpowered.anvil.api.registry

import org.anvilpowered.anvil.api.registry.key.Key
import kotlin.properties.ReadOnlyProperty

fun <T, V : Any> Registry.delegated(key: Key<V>): ReadOnlyProperty<T, V?> = ReadOnlyProperty { _, _ -> get(key) }
