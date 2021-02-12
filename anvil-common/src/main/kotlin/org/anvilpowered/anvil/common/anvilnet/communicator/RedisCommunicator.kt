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
package org.anvilpowered.anvil.common.anvilnet.communicator

import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.messaging.RedisService
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.registry.RegistryScoped
import org.anvilpowered.anvil.common.anvilnet.packet.AnvilNetPacket
import redis.clients.jedis.BinaryJedisPubSub
import java.io.ByteArrayInputStream

@Singleton
class RedisCommunicator @Inject constructor(registry: Registry) : BroadcastCommunicator("Redis") {

  @Inject
  private lateinit var redisService: RedisService

  private val redisListener = object : BinaryJedisPubSub() {
    override fun onMessage(channel: ByteArray, message: ByteArray) {
      accept(ByteArrayInputStream(message))
    }
  }

  init {
    registry.whenLoaded { loaded() }.order(-5).register()
    isAvailable = false
  }

  @RegistryScoped
  private fun loaded() {
    isAvailable = try {
      redisService.jedisPool.resource.subscribe(redisListener, channel)
      true
    } catch (e: Exception) {
      logger.warn("Unable to connect to redis, falling back to plugin messaging only.")
      false
    }
  }

  override fun setListener(nodeId: Int, listener: ((AnvilNetPacket) -> Unit)?) {
    if (!isAvailable) {
      return
    }
    val newTarget = this.nodeId != nodeId
    if (newTarget && this.nodeId != 0) {
      redisListener.unsubscribe(getChannel(this.nodeId))
    }
    super.setListener(nodeId, listener)
    if (newTarget && nodeId != 0) {
      redisListener.subscribe(getChannel(nodeId))
    }
  }

  public override fun send(header: NetworkHeader, data: ByteArray): Boolean {
    redisService.jedisPool.resource.publish(getChannel(header.path.targetId), data)
    return true
  }
}
