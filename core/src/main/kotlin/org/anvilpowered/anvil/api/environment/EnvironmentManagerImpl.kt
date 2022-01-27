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
package org.anvilpowered.anvil.api.environment

import java.util.TreeSet
import java.util.regex.Pattern
import java.util.stream.Stream

class EnvironmentManagerImpl : EnvironmentManager {

    override var environments: MutableMap<String, Environment> = HashMap()
    val pluginEnvironmentMap: MutableMap<Any, MutableSet<Environment>> = HashMap()

    override val coreEnvironment: Environment
        get() = checkNotNull(environments["anvil"]) { "Global environment not loaded" }

    override fun getEnvironmentUnsafe(name: String): Environment {
        return checkNotNull(environments[name]) { "Could not find an environment matching $name" }
    }

    override fun getEnvironmentsAsStream(pattern: Pattern): Stream<Environment> {
        return environments.entries.stream().filter { (key): Map.Entry<String, Environment> ->
            pattern.matcher(key).matches()
        }.map { (_, value) -> value }
    }

    override fun getEnvironment(pattern: Pattern): Environment? {
        return getEnvironmentsAsStream(pattern).findFirst().orElse(null)
    }

    override fun getEnvironment(name: String): Environment? {
        return environments[name]
    }

    fun registerEnvironment(environment: Environment, plugin: Any) {
        val name = environment.name
        require(!environments.containsKey(name)) { "Environment $name already exists" }
        environments[name] = environment
        var envs = pluginEnvironmentMap[plugin]
        if (envs == null) {
            envs = TreeSet()
            envs.add(environment)
            pluginEnvironmentMap[plugin] = envs
        } else {
            envs.add(environment)
        }
    }
}