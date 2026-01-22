package org.anvilpowered.anvil.core.server

import java.net.InetSocketAddress

case class ServerInfo(
    name: String,
    address: InetSocketAddress,
)

object ServerInfo {
  given ordering: Ordering[ServerInfo] = Ordering.by(_.name)
}
