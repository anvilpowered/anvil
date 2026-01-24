package org.anvilpowered.anvil.platform.paper

import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.EventHandler
import org.bukkit.NamespacedKey
import cats.effect.Async

class ServerSender extends Listener {

  @EventHandler
  def onPlayerMove[F[_]: Async](event: PlayerMoveEvent): Unit = {
    event.getPlayer().storeCookie(NamespacedKey.fromString("test"), Array(1))
    event.getPlayer.transfer("test", 22)
  }

}
