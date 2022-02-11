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
package org.anvilpowered.anvil.core.messaging

import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.messaging.RedisService
import org.anvilpowered.anvil.api.registry.AnvilKeys
import org.anvilpowered.registry.Registry
import org.anvilpowered.registry.scope.RegistryScoped
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisPubSub
import java.util.concurrent.CompletableFuture

@Singleton
open class CommonRedisService @Inject protected constructor(private val registry: Registry) : RedisService {

    @RegistryScoped
    override var jedisPool: JedisPool? = null
        get() {
            if (field == null) {
                loadJedis()
                return field!!
            }
            return field!!
        }

    private fun loadJedis() {
        jedisPool = if (registry.getOrDefault(AnvilKeys.REDIS_USE_AUTH)) {
            JedisPool(
                JedisPoolConfig(),
                registry.getOrDefault(AnvilKeys.REDIS_HOSTNAME),
                registry.getOrDefault(AnvilKeys.REDIS_PORT),
                30,
                registry.getOrDefault(AnvilKeys.REDIS_PASSWORD)
            )
        } else {
            JedisPool(
                JedisPoolConfig(),
                registry.getOrDefault(AnvilKeys.REDIS_HOSTNAME),
                registry.getOrDefault(AnvilKeys.REDIS_PORT),
                30
            )
        }
    }

    override fun registerSubscriber(subscriber: JedisPubSub?) {
        CompletableFuture.runAsync {
            jedisPool?.resource
                ?.subscribe(subscriber, registry.getOrDefault(AnvilKeys.SERVER_NAME))
        }
    }
}