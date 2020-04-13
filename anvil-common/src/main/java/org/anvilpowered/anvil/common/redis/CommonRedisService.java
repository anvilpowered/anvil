/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.common.redis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.redis.RedisService;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;

@Singleton
public class CommonRedisService implements RedisService {

    private Registry registry;

    private JedisPool jedisPool;

    @Inject
    protected CommonRedisService(Registry registry) {
        this.registry = registry;
        registry.whenLoaded(this::registryLoaded);
    }

    private void registryLoaded() {
        jedisPool = null;
    }

    private void loadJedis() {
        if (registry.getOrDefault(Keys.REDIS_USE_AUTH)) {
            jedisPool = new JedisPool(
                new JedisPoolConfig(),
                registry.getOrDefault(Keys.REDIS_HOSTNAME),
                registry.getOrDefault(Keys.REDIS_PORT),
                30,
                registry.getOrDefault(Keys.REDIS_PASSWORD)
            );
        } else {
            jedisPool = new JedisPool(
                new JedisPoolConfig(),
                registry.getOrDefault(Keys.REDIS_HOSTNAME),
                registry.getOrDefault(Keys.REDIS_PORT),
                30
            );
        }
    }

    @Override
    public JedisPool getJedisPool() {
        if (jedisPool == null) {
            loadJedis();
        }
        return jedisPool;
    }

    @Override
    public void registerSubscriber(JedisPubSub subscriber) {
        CompletableFuture.runAsync(() -> getJedisPool().getResource()
            .subscribe(subscriber, registry.getOrDefault(Keys.SERVER_NAME)));
    }
}
