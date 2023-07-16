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
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.sourcegrade.kontour.Dto
import org.sourcegrade.kontour.UUID
import kotlin.reflect.KClass

object UserRepositoryImpl : UserRepository {
    override suspend fun findByGameUserId(id: UUID): User? = newSuspendedTransaction {
        // TODO: Does this send two queries to the database?
        GameUserEntity.findById(id)?.let { User(it.user.id.value) }
    }

    override suspend fun findByUsername(username: String): User? = newSuspendedTransaction {
        UserEntity.find { UserTable.username eq username }.firstOrNull()?.let { User(it.id.value) }
    }

    override suspend fun countAll(): Long = newSuspendedTransaction { UserEntity.all().count() }

    override suspend fun create(item: User.CreateDto): User = newSuspendedTransaction {
        UserEntity.new {
            username = item.username
        }.let { User(it.id.value) }
    }

    override suspend fun <D : Dto<User>> findDtoById(id: UUID, dtoType: KClass<D>): D? {
        TODO("Not yet implemented")
    }

    override suspend fun exists(id: UUID): Boolean =
        newSuspendedTransaction { UserEntity.findById(id) != null }

    override suspend fun deleteById(id: UUID): Boolean = newSuspendedTransaction {
        UserTable.deleteWhere { UserTable.id eq id } > 0
    }
}
