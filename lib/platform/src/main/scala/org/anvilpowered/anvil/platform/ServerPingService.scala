/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2026 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.platform

import cats.effect.Async
import net.kyori.adventure.text.Component
import ServerPingService.Players
import ServerPingService.Response

import java.util.UUID

trait ServerPingService {
  def ping[F[_]: Async](server: Server): F[Response]
}

object ServerPingService {
  extension (server: Server)(using ps: ServerPingService) {
    def ping[F[_]: Async]: F[Response] = ps.ping(server)
  }
  // TODO: Create multiple ping variants with different information
  case class Response(players: Players, description: Component, plugins: List[PluginMeta])
  case class Players(online: Int, max: Int, sample: SamplePlayer)
  case class SamplePlayer(name: String, id: UUID)
}
