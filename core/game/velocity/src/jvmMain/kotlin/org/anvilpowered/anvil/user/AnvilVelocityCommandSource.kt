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

import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.anvil.domain.user.Audience
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.anvil.domain.user.Subject
import org.anvilpowered.anvil.domain.user.User
import com.velocitypowered.api.command.CommandSource as VelocityCommandSource
import com.velocitypowered.api.proxy.Player as VelocityPlayer

fun VelocityCommandSource.toAnvilCommandSource(): CommandSource = AnvilVelocityCommandSource(this)

private class AnvilVelocityCommandSource(
    velocityCommandSource: VelocityCommandSource,
) : CommandSource {
    override val audience: Audience = velocityCommandSource
    override val subject: Subject = velocityCommandSource.toAnvilSubject()
    override val player: Player? = (velocityCommandSource as? VelocityPlayer)?.toAnvilPlayer()
    override val gameUser: GameUser? = player?.gameUser

    override suspend fun getUserOrNull(): User? = player?.getUserOrNull()
}
