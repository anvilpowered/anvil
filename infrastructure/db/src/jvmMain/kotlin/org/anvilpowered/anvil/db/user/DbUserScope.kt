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

import org.anvilpowered.anvil.api.user.UserDto
import org.anvilpowered.anvil.domain.system.GameType
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.User
import org.anvilpowered.anvil.user.GameUserFactory
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.sourcegrade.kontour.Crypto
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.Dto
import org.sourcegrade.kontour.UUID
import org.sourcegrade.kontour.UnknownDtoException
import kotlin.reflect.KClass

context(GameUserFactory.Scope)
internal class DbUserScope : User.DbScope {

    context(User)
    private fun DbUser.Companion.findByContextId(): DbUser? = DbUser.findById(id)

    override suspend fun User.getUsername(): String {
        return newSuspendedTransaction {
            DbUser.findByContextId()?.username ?: throw NoSuchElementException()
        }
    }

    override suspend fun User.getEmail(): String = newSuspendedTransaction {
        DbUser.findByContextId()?.email ?: throw NoSuchElementException()
    }

    override suspend fun User.getGameUsers(gameType: GameType?): List<GameUser> = newSuspendedTransaction {
        (DbUser.findByContextId() ?: throw NoSuchElementException())
            .gameUsers.map { gameUserFactory.createGameUser(it.id.value) }
    }

    override suspend fun DomainEntity.Repository<User>.findByUsername(username: String): User? = newSuspendedTransaction {
        DbUser.find { Users.username eq username }.firstOrNull()?.let { User(it.id.value) }
    }

    override suspend fun DomainEntity.Repository<User>.create(item: User.CreateDto): User = newSuspendedTransaction {
        DbUser.new(Crypto.randomUUID()) {
            username = item.username
            email = item.email
        }.id.value.let(::User)
    }

    override suspend fun User.exists(): Boolean = newSuspendedTransaction {
        DbUser.findByContextId() != null
    }

    override suspend fun <D : Dto<User>> DomainEntity.Repository<User>.findDtoById(
        id: UUID, dtoType: KClass<D>,
    ): D? = newSuspendedTransaction {
        DbUser.findById(id)?.let {
            @Suppress("UNCHECKED_CAST")
            when (dtoType) {
                UserDto.Basic::class -> UserDto.Basic(id, it.username)
                UserDto.Full::class -> UserDto.Full(id, it.username, it.email)
                else -> throw UnknownDtoException(dtoType, User::class)
            } as D
        }
    }

    override suspend fun DomainEntity.Repository<User>.deleteById(id: UUID): Boolean = newSuspendedTransaction {
        DbUser.findById(id)?.delete() != null
    }
}
