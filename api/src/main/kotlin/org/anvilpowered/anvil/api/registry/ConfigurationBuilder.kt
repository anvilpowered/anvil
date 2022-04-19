package org.anvilpowered.anvil.api.registry

import org.anvilpowered.anvil.api.registry.key.Key

sealed interface TreeElement

data class TreeObject(
    val backingValues: Map<String, TreeElement>,
    val backingDescriptions: Map<String, String>,
) : TreeElement

data class TreePrimitive(val key: Key<*>) : TreeElement

class TreeObjectBuilder {
    internal val backingValues = mutableMapOf<String, TreeElement>()
    internal val backingDescriptions = mutableMapOf<String, String>()
    internal val self get() = this
    internal fun build() = TreeObject(backingValues.toMap(), backingDescriptions.toMap())
}

fun config(build: TreeObjectBuilder.() -> Unit) = TreeObjectBuilder().also(build).build()

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
    backingValues[this] = TreePrimitive(key)
    return ByHandle(self, this)
}

context(TreeObjectBuilder)
    infix fun String.by(build: TreeObjectBuilder.() -> Unit) {
    backingValues[this] = TreeObjectBuilder().also(build).build()
}
