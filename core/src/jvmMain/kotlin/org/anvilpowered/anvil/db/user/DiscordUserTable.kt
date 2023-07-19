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

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

internal object DiscordUserTable : UUIDTable("discord_users") {
    val discordId = ulong("discord_id").uniqueIndex()
}

internal class DiscordUserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    var discordId by DiscordUserTable.discordId.transform({ it.toULong() }, { it.toString() })
    val user: UserEntity by UserEntity referencedOn DiscordUserTable.id

    companion object : UUIDEntityClass<DiscordUserEntity>(DiscordUserTable)
}
