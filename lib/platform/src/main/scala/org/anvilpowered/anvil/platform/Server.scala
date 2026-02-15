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

import org.anvilpowered.anvil.chat.WithAudience

import java.net.InetSocketAddress
import org.anvilpowered.anvil.chat.Audience

case class Server(
    name: String,
    address: InetSocketAddress,
    platform: Platform,
    audience: Audience,
)

object Server {
  given ordering: Ordering[Server] = Ordering.by(_.name)
  given withAudience: WithAudience[Server] = _.audience
}
// trait Server2 {
//   val broadcastAudience: Audience
//
//   val systemSubject: Audience
// }
