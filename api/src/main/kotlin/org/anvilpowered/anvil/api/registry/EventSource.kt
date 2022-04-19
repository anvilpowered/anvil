package org.anvilpowered.anvil.api.registry

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

interface EventSource{

    val flow: SharedFlow<Any>
}

fun <T : Any> EventSource.listen(type: KClass<T>, listener: (T) -> Unit) {
    flow.onEach { type.safeCast(it)?.also(listener) }
}

inline fun <reified T : Any> EventSource.listen(noinline listener: (T) -> Unit) = listen(T::class, listener)
