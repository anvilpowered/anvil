package org.anvilpowered.anvil.api

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key

object Audiences {

  private val audienceMap: MutableMap<Key, Audience> = mutableMapOf()
  val permissionMap: MutableMap<Key, String> = mutableMapOf()

  fun create(key: Key, vararg audience: Audience) {
    if (audienceMap.containsKey(key)) {
      val existing = mutableListOf(audienceMap[key] ?: Audience.empty())
      for (a in audience) {
        existing.add(a)
      }
      audienceMap[key] = Audience.audience(existing)
      return
    }
    audienceMap[key] = Audience.audience(*audience)
  }

  fun create(key: Key, permission: String, vararg audience: Audience?) {
    audienceMap[key] = Audience.audience(*audience )
    permissionMap[key] = permission
    if (!audience.isNullOrEmpty()) {
      create(key, Audience.audience(*audience))
    }
  }

  fun add(key: Key, vararg audience: Audience) {
    if (!audienceMap.containsKey(key)) {
      create(key, *audience)
    } else {
      audienceMap[key] = Audience.audience(audienceMap[key], *audience)
    }
  }
}
