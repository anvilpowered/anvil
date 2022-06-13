package org.anvilpowered.anvil.api.registry

import org.anvilpowered.anvil.api.registry.key.Key

sealed interface ConfigElement

data class ConfigSchema(
    val backingValues: Map<String, ConfigElement>,
    val backingDescriptions: Map<String, String>,
    val keys: Map<Key<*>, KeySchemaEntry<*>>,
) : ConfigElement

data class ConfigPrimitive(val key: Key<*>) : ConfigElement

data class KeySchemaEntry<T : Any>(
    val key: Key<T>,
    val fqName: List<String>,
    val description: String?,
)

@Suppress("UNCHECKED_CAST")
operator fun <T : Any> ConfigSchema.get(key: Key<T>): KeySchemaEntry<T> = keys[key] as KeySchemaEntry<T>

class TreeObjectBuilder {
    internal val backingValues = mutableMapOf<String, ConfigElement>()
    internal val backingDescriptions = mutableMapOf<String, String>()
    internal val self get() = this

    /**
     * Performs a depth-first search of [backingValues] and [backingDescriptions] and adds all discovered entries to [flatKeys]
     */
    private fun traverse(path: List<String>, flatKeys: MutableMap<Key<*>, KeySchemaEntry<*>>) {
        for ((name, element) in backingValues) {
            when (element) {
                is ConfigSchema -> traverse(path + name, flatKeys)
                is ConfigPrimitive -> flatKeys.compute(element.key) { _, v ->
                    check(v == null) { "Duplicate definition in schema for key ${element.key}" }
                    KeySchemaEntry(element.key, path + name, backingDescriptions[name])
                }
            }
        }
    }

    internal fun build(): ConfigSchema {
        val flatKeys = mutableMapOf<Key<*>, KeySchemaEntry<*>>()
        traverse(emptyList(), flatKeys)
        return ConfigSchema(backingValues.toMap(), backingDescriptions.toMap(), flatKeys.toMap())
    }
}

fun config(build: TreeObjectBuilder.() -> Unit): ConfigSchema = TreeObjectBuilder().also(build).build()

class ByHandle internal constructor(
    private val treeObjectBuilder: TreeObjectBuilder,
    private val name: String,
) {
    infix fun describe(description: String) {
        treeObjectBuilder.backingDescriptions[name] = description
    }
}

context(TreeObjectBuilder)
infix fun String.by(key: Key<*>): ByHandle {
    backingValues[this] = ConfigPrimitive(key)
    return ByHandle(self, this)
}

context(TreeObjectBuilder)
    infix fun String.by(build: TreeObjectBuilder.() -> Unit) {
    backingValues[this] = TreeObjectBuilder().also(build).build()
}
