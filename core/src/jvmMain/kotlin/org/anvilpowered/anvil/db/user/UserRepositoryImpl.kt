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

import org.anvilpowered.anvil.domain.user.User
import org.anvilpowered.anvil.domain.user.UserRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.sourcegrade.kontour.UUID

object UserRepositoryImpl : UserRepository {

    override suspend fun initializeFromGameUser(
        gameUserId: UUID,
        username: String,
    ): User = newSuspendedTransaction {
        val userId = GameUserTable
            .slice(listOf(GameUserTable.id, GameUserTable.userId))
            .select { GameUserTable.id eq gameUserId }
            .firstOrNull()
            ?.get(GameUserTable.userId)

        // if a game user exists, a user must also already exist

        userId?.let {
            return@let User(it.value, username)
        }

        // otherwise, create a new user and game user

        val user = UserEntity.new {
            this.username = username
        }

        GameUserEntity.new {
            this.user = user
            this.gameType
        }.let { User(it.id.value, username) }
    }

    override suspend fun findByGameUserId(id: UUID): User? = newSuspendedTransaction {
        GameUserTable.innerJoin(UserTable)
            .select { GameUserTable.id eq id }
            .firstOrNull()
            ?.toUser()
    }

    override suspend fun findByUsername(username: String): User? = newSuspendedTransaction {
        UserTable.select { UserTable.username eq username }
            .firstOrNull()
            ?.toUser()
    }

    override suspend fun countAll(): Long = newSuspendedTransaction { UserEntity.all().count() }

    override suspend fun create(item: User.CreateDto): User = newSuspendedTransaction {
        UserEntity.new {
            username = item.username
        }.let { User(it.id.value, item.username, item.email) }
    }

    override suspend fun exists(id: UUID): Boolean = newSuspendedTransaction {
        UserEntity.findById(id) != null
    }

    override suspend fun deleteById(id: UUID): Boolean = newSuspendedTransaction {
        UserTable.deleteWhere { UserTable.id eq id } > 0
    }
}
