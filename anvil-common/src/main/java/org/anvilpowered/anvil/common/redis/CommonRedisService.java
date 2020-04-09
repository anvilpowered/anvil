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
