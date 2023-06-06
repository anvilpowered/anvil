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

package org.anvilpowered.anvil.db.user

import org.anvilpowered.anvil.db.system.DbGameType
import org.anvilpowered.anvil.db.system.GameTypes
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.sourcegrade.kontour.UUID

object GameUsers : UUIDTable("game_users") {
    val userId = reference("user_id", Users)
    val gameTypeId = reference("game_type_id", GameTypes)
    val username = varchar("username", 255).uniqueIndex()
    val nickname = varchar("nickname", 255).nullable()
}

class DbGameUser(id: EntityID<UUID>) : UUIDEntity(id) {
    var user: DbUser by DbUser referencedOn GameUsers.userId
    var gameType: DbGameType by DbGameType referencedOn GameUsers.gameTypeId
    var username: String by GameUsers.username
    var nickname: String? by GameUsers.nickname

    companion object : UUIDEntityClass<DbGameUser>(GameUsers)
}
