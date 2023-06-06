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

package org.anvilpowered.anvil.user

import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.Player
import org.sourcegrade.kontour.UUID

/**
 * Velocity does not have an offline player concept, so we must handle it ourselves.
 */
internal class AnvilVelocityGameUser(
    override val id: UUID,
) : GameUser {
    override val username: String
        get() = TODO("Not yet implemented")
    override val player: Player?
        get() = TODO("Not yet implemented")

    override fun hasPermission(permission: String): Boolean? {
        TODO("Not yet implemented")
    }

}
