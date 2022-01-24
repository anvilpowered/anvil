/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.api.entity

import java.util.Optional
import java.util.UUID

interface RestrictionService {
    /**
     *
     *
     * Restricts the provided entity as specified by the provided [RestrictionCriteria], overwriting any
     * previous restrictions.
     *
     * <br></br>
     *
     *
     * If the provided entity is identifiable by [UUID], the provided [RestrictionCriteria]
     * will be registered with said [UUID] rather than the provided entity instance.
     *
     *
     * @param entity   The entity to restrict
     * @param criteria The [RestrictionCriteria] to restrict with
     */
    fun put(entity: Any, criteria: RestrictionCriteria)

    /**
     *
     *
     * Restricts entities identified by the provided [UUID] as specified by the provided
     * [RestrictionCriteria], overwriting any previous restrictions.
     *
     *
     * @param uuid     The uuid of the entity to restrict
     * @param criteria The [RestrictionCriteria] to restrict with
     */
    fun put(uuid: UUID, criteria: RestrictionCriteria)

    /**
     *
     *
     * Stops all restrictions for the provided entity.
     *
     * <br></br>
     *
     *
     * If the provided entity is identifiable by [UUID], removes all restrictions for entities identified by
     * said [UUID].
     *
     *
     * @param entity The entity to stop restrictions for
     * @return An [Optional] containing the removed [RestrictionCriteria] if it existed,
     * otherwise [Optional.empty]
     */
    fun remove(entity: Any): Optional<RestrictionCriteria>

    /**
     *
     *
     * Stops all restrictions for entities identified by the provided [UUID].
     *
     *
     * @param uuid The uuid of the entity to stop restricting
     * @return An [Optional] containing the removed [RestrictionCriteria] if it existed,
     * otherwise [Optional.empty]
     */
    fun remove(uuid: UUID): Optional<RestrictionCriteria>

    /**
     *
     *
     * Checks whether the provided entity is restricted.
     *
     *
     * @param entity The entity to check
     * @return The [RestrictionCriteria] associated with the provided entity, otherwise
     * [RestrictionCriteria.none]
     */
    operator fun get(entity: Any): RestrictionCriteria

    /**
     *
     *
     * Checks whether the entity identified by the provided [UUID] is restricted.
     *
     *
     * @param uuid The [UUID] of the entity to check
     * @return The [RestrictionCriteria] associated with the provided [UUID], otherwise
     * [RestrictionCriteria.none]
     */
    operator fun get(uuid: UUID): RestrictionCriteria
}
