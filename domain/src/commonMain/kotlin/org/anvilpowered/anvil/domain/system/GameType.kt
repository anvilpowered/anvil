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

package org.anvilpowered.anvil.domain.system

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID
import org.sourcegrade.kontour.scope.CrudScope

data class GameType(override val id: UUID) : DomainEntity {

    data class CreateDto(
        val name: String,
        val website: String,
    ) : Creates<GameType>

    interface DbScope : CrudScope<GameType, CreateDto> {

        suspend fun GameType.getName(): String

        suspend fun GameType.getWebsite(): String

        suspend fun DomainEntity.Repository<GameType>.findByName(name: String): GameType?

        suspend fun DomainEntity.Repository<GameType>.findByWebsite(website: String): GameType?
    }

    companion object Repository : DomainEntity.Repository<GameType>
}
