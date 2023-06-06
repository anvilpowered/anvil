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

package org.anvilpowered.anvil.db.system

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object GameTypes : UUIDTable("game_types") {
    val name = varchar("name", 255).uniqueIndex()
    val website = varchar("website", 255)
}

class DbGameType(id: EntityID<UUID>) : UUIDEntity(id) {
    var name by GameTypes.name
    var website by GameTypes.website

    companion object : UUIDEntityClass<DbGameType>(GameTypes)
}

//context(GameTypes)
//fun InsertStatement<*>.setValuesFrom(gameType: GameType.CreateDto) {
//    this[name] = gameType.name
//    this[website] = gameType.website
//}
//
//fun ResultRow.toGameType() = GameType(
//    name = this[GameTypes.name],
//    website = this[GameTypes.website],
//    id = this[GameTypes.id].value,
//)
