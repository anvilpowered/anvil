package org.anvilpowered.anvil.core.platform

import cats.effect.Async
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.core.platform.ServerPingService.Players
import org.anvilpowered.anvil.core.platform.ServerPingService.Response

import java.util.UUID

trait ServerPingService {
  def ping[F[_]: Async](server: Server): F[Response]
}

object ServerPingService {
  extension (server: Server)(using ps: ServerPingService) {
    def ping[F[_]: Async]: F[Response] = ps.ping(server)
  }
  case class Response(players: Players, description: Component)
  case class Players(online: Int, max: Int, sample: SamplePlayer)
  case class SamplePlayer(name: String, id: UUID)
}
