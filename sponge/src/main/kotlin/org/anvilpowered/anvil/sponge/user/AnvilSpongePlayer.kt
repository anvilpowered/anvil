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

import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import java.util.UUID

fun ServerPlayer.toAnvilPlayer(): Player = AnvilSpongePlayer(this)

private class AnvilSpongePlayer(
    override val platformDelegate: ServerPlayer,
) : Player,
    Subject by platformDelegate.toAnvilSubject() {
    override val id: UUID = platformDelegate.uniqueId()
    override val username: String = platformDelegate.name()
    override val displayname: Component = platformDelegate.displayName().get()
    override val latencyMs: Int
        get() = platformDelegate.connection().latency()
}
