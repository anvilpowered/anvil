/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
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

package org.anvilpowered.anvil.velocity.user

import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.PlayerService
import org.anvilpowered.anvil.velocity.ProxyServerScope
import java.util.UUID

context(ProxyServerScope)
class VelocityPlayerService : PlayerService {
    override fun get(username: String): Player? =
        proxyServer.getPlayer(username).orElse(null)?.toAnvilPlayer()

    override fun get(id: UUID): Player? =
        proxyServer.getPlayer(id).orElse(null)?.toAnvilPlayer()

    override fun getAll(startsWith: String): Sequence<Player> =
        proxyServer.allPlayers.asSequence().map { it.toAnvilPlayer() }
}
