package org.anvilpowered.anvil.core.server

import org.anvilpowered.anvil.core.user.Player

case class RegisteredServer(
  serverInfo: ServerInfo,
  players: Seq[Player],
)
