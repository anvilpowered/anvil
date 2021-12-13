/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.paper.util

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.chat.TextComponent
import org.anvilpowered.anvil.api.util.KickService
import org.anvilpowered.anvil.api.util.UserService
import org.bukkit.entity.Player
import java.util.UUID

class PaperKickService : KickService {

    @Inject
    private lateinit var userService: UserService<Player, Player>

    private fun getReason(reason: Any): TextComponent {
        return if (reason is TextComponent) reason else TextComponent(reason.toString())
    }

    override fun kick(userUUID: UUID, reason: Any) {
        userService.getPlayer(userUUID).ifPresent { player: Player -> player.kick(Component.text(reason.toString())) }
    }

    override fun kick(userName: String, reason: Any) {
        userService.getPlayer(userName).ifPresent { player: Player -> player.kick(Component.text(reason.toString())) }
    }

    override fun kick(userUUID: UUID) {
        kick(userUUID, "You have been kicked")
    }

    override fun kick(userName: String) {
        kick(userName, "You have been kicked")
    }
}
