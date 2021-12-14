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

import org.anvilpowered.anvil.common.util.CommonUserService
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors
import java.util.stream.Stream

class PaperUserService : CommonUserService<Player, Player>(Player::class.java) {


    override fun get(userName: String): Optional<Player> = Optional.ofNullable(Bukkit.getPlayer(userName))
    override fun get(userUUID: UUID): Optional<Player> = Optional.ofNullable(Bukkit.getPlayer(userUUID))
    override fun getPlayer(userName: String): Optional<Player> = get(userName)
    override fun getPlayer(userUUID: UUID): Optional<Player> = get(userUUID)
    override fun getPlayer(player: Player): Optional<Player> = Optional.of(player)
    override fun getOnlinePlayers(): Collection<Player> = Bukkit.getOnlinePlayers()
    override fun getUUID(user: Player): UUID = user.uniqueId
    override fun getUserName(user: Player): String = user.name

    override fun matchPlayerNames(startsWith: String): List<String> {
        val startsWithLowerCase = startsWith.toLowerCase()
        return Stream.of(*Bukkit.getOfflinePlayers())
            .map { obj: OfflinePlayer -> obj.name }
            .filter { Objects.nonNull(it) }
            .filter { it!!.toLowerCase().startsWith(startsWithLowerCase) }
            .collect(Collectors.toList()) as List<String>
    }

    override fun getUUID(userName: String): CompletableFuture<Optional<UUID>> {
        val userUUID = getPlayer(userName).map { obj: Player -> obj.uniqueId }
        return if (userUUID.isPresent) {
            CompletableFuture.completedFuture(userUUID)
        } else super.getUUID(userName)
    }

    override fun getUserName(userUUID: UUID): CompletableFuture<Optional<String>> {
        val userName = getPlayer(userUUID).map { obj: Player -> obj.name }
        return if (userName.isPresent) {
            CompletableFuture.completedFuture(userName)
        } else super.getUserName(userUUID)
    }
}
