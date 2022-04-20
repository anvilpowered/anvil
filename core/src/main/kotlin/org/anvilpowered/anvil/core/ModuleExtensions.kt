package org.anvilpowered.anvil.core

import com.google.inject.Binder
import com.google.inject.binder.AnnotatedBindingBuilder
import com.google.inject.binder.LinkedBindingBuilder
import com.google.inject.binder.ScopedBindingBuilder

inline fun <reified T> Binder.bind(): AnnotatedBindingBuilder<T> = bind(T::class.java)

inline fun <reified T> LinkedBindingBuilder<in T>.to(): ScopedBindingBuilder = to(T::class.java)
