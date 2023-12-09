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

import net.kyori.adventure.audience.Audience
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.entity.living.player.server.ServerPlayer

fun CommandContext.toAnvilCommandSource(): CommandSource = AnvilSpongeCommandSource(this)

class AnvilSpongeCommandSource(
    override val platformDelegate: CommandContext,
) : CommandSource {
    override val audience: Audience = platformDelegate.cause().audience()
    override val subject: Subject = platformDelegate.cause().subject().toAnvilSubject()
    override val player: Player? = platformDelegate.cause().first(ServerPlayer::class.java).orElse(null)?.toAnvilPlayer()
}
