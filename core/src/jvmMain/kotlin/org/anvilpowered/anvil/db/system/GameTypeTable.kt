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
import org.jetbrains.exposed.sql.SizedIterable
import java.util.UUID

internal object GameTypeTable : UUIDTable("game_types") {
    val name = varchar("name", 255).uniqueIndex()
    val website = varchar("website", 255)
}

internal class GameTypeEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    var name by GameTypeTable.name
    var website by GameTypeTable.website
    val serverNodes: SizedIterable<ServerNodeEntity> by ServerNodeEntity referrersOn ServerNodeTable.gameTypeId

    companion object : UUIDEntityClass<GameTypeEntity>(GameTypeTable)
}
