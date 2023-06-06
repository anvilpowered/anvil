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
import org.anvilpowered.anvil.domain.user.Subject
import org.sourcegrade.kontour.UUID
import kotlin.jvm.optionals.getOrNull
import org.spongepowered.api.entity.living.player.User as SpongeUser

fun SpongeUser.toAnvilGameUser(): GameUser = SpongeGameUser(this)

private class SpongeGameUser(
    val spongeUser: SpongeUser,
) : GameUser,
    Subject by spongeUser.toAnvilSubject() {
    override val id: UUID
        get() = spongeUser.uniqueId()
    override val username: String
        get() = spongeUser.name()
    override val player: Player?
        get() = spongeUser.player().getOrNull()?.toAnvilPlayer()
}
