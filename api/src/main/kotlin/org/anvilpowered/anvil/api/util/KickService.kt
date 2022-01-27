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
package org.anvilpowered.anvil.api.util

import java.util.UUID

interface KickService {

    /**
     * Kicks a player based off of their [userUUID] with the given [reason]
     *
     * @param userUUID The [UUID] to kick
     * @param reason The reason for kicking.
     *
     * @sample kick(userUUID, Component.text("You have been kicked!"))
     * @sample kick(userUUID, "&4You have been kicked!")
     */
    fun kick(userUUID: UUID, reason: Any)

    /**
     * Kicks a player based off of their [userName] with the given [reason]
     *
     * @param userName The [userName] to kick
     * @param reason The reason for kicking.
     *
     * @sample kick(userName, Component.text("You have been kicked!"))
     * @sample kick(userName, "&4You have been kicked!")
     */
    fun kick(userName: String, reason: Any)


    /**
     * Kicks a player based off of their [userUUID]
     *
     * @param userUUID The [UUID] to kick
     *
     * @sample kick(userUUID)
     */
    fun kick(userUUID: UUID)

    /**
     * Kicks a player based off of their [userName]
     *
     * @param userName The [userName] to kick
     *
     * @sample kick(userName)
     */
    fun kick(userName: String)
}
