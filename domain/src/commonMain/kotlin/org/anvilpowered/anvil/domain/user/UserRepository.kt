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

package org.anvilpowered.anvil.domain.user

import org.sourcegrade.kontour.Repository
import org.sourcegrade.kontour.UUID

interface UserRepository : Repository<User, User.CreateDto> {
    /**
     * This method is used to initialize a user from a game user.
     * If a game user with the given ID does not exist, it will be created.
     * Call this to ensure that the given game user has a corresponding user in the database.
     *
     * This method is idempotent.
     */
    suspend fun initializeFromGameUser(gameUserId: UUID, username: String): User
    suspend fun findByGameUserId(id: UUID): User?
    suspend fun findByUsername(username: String): User?
}
