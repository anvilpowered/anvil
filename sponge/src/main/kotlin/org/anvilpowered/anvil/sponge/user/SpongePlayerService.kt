/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
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

package org.anvilpowered.anvil.sponge.user

import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.PlayerService
import org.spongepowered.api.Sponge
import java.util.UUID

object SpongePlayerService : PlayerService {
    override fun get(username: String): Player? =
        Sponge.server().player(username).orElse(null)?.toAnvilPlayer()

    override fun get(id: UUID): Player? =
        Sponge.server().player(id).orElse(null)?.toAnvilPlayer()

    override fun getAll(startsWith: String): Sequence<Player> = when (startsWith) {
        "" -> Sponge.server().onlinePlayers().asSequence().map { it.toAnvilPlayer() }
        else -> Sponge.server().onlinePlayers().asSequence().filter { it.name().startsWith(startsWith) }.map { it.toAnvilPlayer() }
    }

    override fun count(): Int = Sponge.server().onlinePlayers().size
}
