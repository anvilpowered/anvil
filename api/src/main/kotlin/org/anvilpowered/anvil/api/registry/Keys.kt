/*
 *   Anvil - AnvilPowered
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
package org.anvilpowered.anvil.api.registry

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Table
import java.time.ZoneId
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
                IllegalArgumentException(
                    "Could not resolve key $name")
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

        val SERVER_NAME = Key.build {
            name("SERVER_NAME")
            fallback("server")
        }
        val TIME_ZONE = Key.build {
            name("TIME_ZONE")
            fallback(ZoneId.systemDefault())
            parser { input: String? -> ZoneIdSerializer.parse(input) }
            toStringer { zoneId: ZoneId? -> ZoneIdSerializer.toString(zoneId) }
        }
        val PROXY_MODE = Key.build {
            name("PROXY_MODE")
            fallback(false)
        }
        val REGEDIT_ALLOW_SENSITIVE = Key.build {
            name("REGEDIT_ALLOW_SENSITIVE")
            fallback(false)
            userImmutable()
        }
        val BASE_SCAN_PACKAGE = Key.build {
            name("BASE_SCAN_PACKAGE")
            fallback("org.anvilpowered.anvil.common.model")
            userImmutable()
        }
        val CACHE_INVALIDATION_INTERVAL_SECONDS = Key.build {
            name("CACHE_INVALIDATION_INTERVAL_SECONDS")
            fallback(30)
        }
        val CACHE_INVALIDATION_TIMOUT_SECONDS = Key.build {
            name("CACHE_INVALIDATION_TIMOUT_SECONDS")
            fallback(300)
        }
        val USE_SHARED_ENVIRONMENT = Key.build {
            name("USE_SHARED_ENVIRONMENT")
            fallback(false)
            sensitive()
        }
        val USE_SHARED_CREDENTIALS = Key.build {
            name("USE_SHARED_CREDENTIALS")
            fallback(false)
                .sensitive()
        }
        val DATA_DIRECTORY: Key<String> = Key.build {
            name("DATA_DIRECTORY")
            fallback("anvil")
            sensitive()
        }
        val DATA_STORE_NAME: Key<String> = Key.build {
            name("DATA_STORE_NAME")
            fallback("xodus")
            sensitive()
        }
        val MONGODB_CONNECTION_STRING: Key<String> = Key.build {
            name("MONGODB_CONNECTION_STRING")
            fallback("mongodb://admin:password@localhost:27017/anvil?authSource=admin")
            sensitive()
        }
        val MONGODB_HOSTNAME = Key.build {
            name("MONGODB_HOSTNAME")
            fallback("localhost")
            sensitive()
        }
        val MONGODB_PORT: Key<Int> = Key.build {
            name("MONGODB_PORT")
            fallback(27017)
            sensitive()
        }
        val MONGODB_DBNAME: Key<String> = Key.build {
            name("MONGODB_DBNAME")
            fallback("anvil")
            sensitive()
        }
        val MONGODB_USERNAME: Key<String> = Key.build {
            name("MONGODB_USERNAME")
            fallback("admin")
            sensitive()
        }
        val MONGODB_PASSWORD: Key<String> = Key.build {
            name("MONGODB_PASSWORD")
            fallback("password")
            sensitive()
        }
        val MONGODB_AUTH_DB: Key<String> = Key.build {
            name("MONGODB_AUTH_DB")
            fallback("admin")
            sensitive()
        }
        val MONGODB_USE_AUTH: Key<Boolean> = Key.build {
            name("MONGODB_USE_AUTH")
            fallback(false)
            sensitive()
        }
        val MONGODB_USE_SRV: Key<Boolean> = Key.build {
            name("MONGODB_USE_SRV")
            fallback(false)
            sensitive()
        }
        val MONGODB_USE_CONNECTION_STRING: Key<Boolean> = Key.build {
            name("MONGODB_USE_CONNECTION_STRING")
            fallback(false)
            sensitive()
        }
        val REDIS_HOSTNAME: Key<String> = Key.build {
            name("REDIS_HOSTNAME")
            fallback("localhost")
            sensitive()
        }
        val REDIS_PORT: Key<Int> = Key.build {
            name("REDIS_PORT")
            fallback(6379)
            sensitive()
        }
        val REDIS_PASSWORD: Key<String> = Key.build {
            name("REDIS_PASSWORD")
            fallback("password")
            sensitive()
        }
        val REDIS_USE_AUTH: Key<Boolean> = Key.build {
            name("REDIS_USE_AUTH")
            fallback(false)
            sensitive()
        }
        val PLUGINS_PERMISSION: Key<String> = Key.build {
            name("PLUGINS_PERMISSION")
            fallback("anvil.admin.plugins")
        }
        val REGEDIT_PERMISSION: Key<String> = Key.build {
            name("REGEDIT_PERMISSION")
            fallback("anvil.admin.regedit")
        }
        val RELOAD_PERMISSION: Key<String> = Key.build {
            name("RELOAD_PERMISSION")
            fallback("anvil.admin.reload")
        }

        init {
            startRegistration(GLOBAL_NAMESPACE)
                .register(SERVER_NAME)
                .register(TIME_ZONE)
                .register(PROXY_MODE)
                .register(REGEDIT_ALLOW_SENSITIVE)
                .register(BASE_SCAN_PACKAGE)
                .register(CACHE_INVALIDATION_INTERVAL_SECONDS)
                .register(CACHE_INVALIDATION_TIMOUT_SECONDS)
                .register(USE_SHARED_ENVIRONMENT)
                .register(USE_SHARED_CREDENTIALS)
                .register(DATA_DIRECTORY)
                .register(DATA_STORE_NAME)
                .register(MONGODB_HOSTNAME)
                .register(MONGODB_PORT)
                .register(MONGODB_DBNAME)
                .register(MONGODB_USERNAME)
                .register(MONGODB_PASSWORD)
                .register(MONGODB_AUTH_DB)
                .register(MONGODB_USE_AUTH)
                .register(MONGODB_USE_SRV)
                .register(REDIS_HOSTNAME)
                .register(REDIS_PORT)
                .register(REDIS_PASSWORD)
                .register(REDIS_USE_AUTH)
            startRegistration("anvil")
                .register(PLUGINS_PERMISSION)
                .register(REGEDIT_PERMISSION)
                .register(RELOAD_PERMISSION)
        }
    }
}
