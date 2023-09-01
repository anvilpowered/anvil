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

import org.anvilpowered.anvil.domain.user.DiscordUser
import org.anvilpowered.anvil.domain.user.DiscordUserRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.sourcegrade.kontour.UUID

object DiscordUserRepositoryImpl : DiscordUserRepository {
    override suspend fun countAll(): Long = newSuspendedTransaction { DiscordUserEntity.all().count() }

    override suspend fun create(item: DiscordUser): DiscordUser = newSuspendedTransaction {
        DiscordUserEntity.new(item.id) {
            discordId = item.discordId
        }
        item
    }

    override suspend fun exists(id: UUID): Boolean = newSuspendedTransaction {
        DiscordUserEntity.findById(id) != null
    }

    override suspend fun deleteById(id: UUID): Boolean = newSuspendedTransaction {
        DiscordUserTable.deleteWhere { DiscordUserTable.id eq id } > 0
    }
}
