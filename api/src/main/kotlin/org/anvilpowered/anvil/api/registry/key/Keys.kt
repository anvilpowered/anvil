/*
 *   Anvil - Registry
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.api.registry.key

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Table
import java.util.Optional

class Keys private constructor() {

    class KeyRegistrationEnd internal constructor(private val nameSpace: String) {
        private fun checkName(nameSpace: String, name: String) {
            require(!keys.contains(nameSpace, name)) {
                ("The provided key $name conflicts with a key of the same name in the $nameSpace namespace")
            }
        }

        /**
         * Registers the provided key.
         *
         * @param key The [Key] to register
         * @return `this`
         */
        fun register(key: Key<*>): KeyRegistrationEnd {
            val name: String = key.name
            checkName(nameSpace, name)
            keys.put(nameSpace, key.name, key)
            return this
        }
    }

    init {
        throw AssertionError("**boss music** No instance for you!")
    }

    companion object {
        private const val GLOBAL_NAMESPACE = "global"
        private val keys: Table<String, String, Key<*>> = HashBasedTable.create()
        private val localAndGlobalCache: MutableMap<String, Map<String, Key<*>>?> = HashMap()

        /**
         *
         *
         * Used to start the registration of keys in a namespace.
         *
         * <br></br>
         *
         *
         * Example usage:
         *
         * <pre>`
         * static {
         * Keys.startRegistration("ontime")
         * .register(RANKS)
         * .register(CHECK_PERMISSION)
         * .register(CHECK_EXTENDED_PERMISSION)
         * .register(EDIT_PERMISSION)
         * .register(IMPORT_PERMISSION);
         * }
        `</pre> *
         *
         * @param nameSpace The namespace to register the keys in. Usually the name of the plugin.
         * @return A [KeyRegistrationEnd] instance for registering keys
         */
        fun startRegistration(nameSpace: String): KeyRegistrationEnd {
            return KeyRegistrationEnd(nameSpace)
        }

        fun <T> resolveUnsafe(name: String, nameSpace: String): Key<T> {
            return keys[nameSpace, name] as Key<T>
        }

        fun <T> resolve(name: String, nameSpace: String): Optional<Key<T>> {
            return Optional.ofNullable(keys[nameSpace, name] as Key<T>)
        }

        fun <T> resolveUnsafe(name: String): Key<T> {
            return resolve<Any>(name).orElseThrow {
                IllegalArgumentException("Could not resolve key $name")
            } as Key<T>
        }

        private fun <T> resolve(name: String): Optional<Key<T>> {
            val candidate = keys[GLOBAL_NAMESPACE, name] as Key<T>?
            if (candidate != null) {
                return Optional.of(candidate)
            }
            val it: Iterator<Key<*>> = keys.column(name).values.iterator()
            return if (it.hasNext()) {
                Optional.of(it.next() as Key<T>)
            } else {
                Optional.empty()
            }
        }

        fun <T> resolveLocalAndGlobal(name: String, nameSpace: String): Optional<Key<T>> {
            val candidate = keys[nameSpace, name] as Key<T>?
            return if (candidate != null) {
                Optional.of(candidate)
            } else Optional.ofNullable(keys[GLOBAL_NAMESPACE, name] as Key<T>)
        }

        fun getAll(nameSpace: String): Map<String, Key<*>>? {
            var result = localAndGlobalCache[nameSpace]
            if (result != null) {
                return result
            }
            result = ImmutableMap.builder<String, Key<*>>()
                .putAll(keys.row(nameSpace))
                .putAll(keys.row(GLOBAL_NAMESPACE))
                .build()
            localAndGlobalCache[nameSpace] = result
            return result
        }

    }
}
