package org.anvilpowered.anvil.core.server

import java.util.UUID
import org.anvilpowered.anvil.core.server.ServerPing.Version
import org.anvilpowered.anvil.core.server.ServerPing.Players
import net.kyori.adventure.text.Component

case class ServerPingResponse(
    version: Version,
    players: Players,
    description: Component,
)

object ServerPing {
  case class Version(protocol: Int, name: String)
  case class Players(online: Int, max: Int, sample: SamplePlayer)
  case class SamplePlayer(name: String, id: UUID)
}
