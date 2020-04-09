package org.anvilpowered.anvil.api.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public interface RedisService {

    JedisPool getJedisPool();

    void registerSubscriber(JedisPubSub subscriber);
}
