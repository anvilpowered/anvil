/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.api.entity;

import java.util.Optional;
import java.util.UUID;

public interface RestrictionService {

    /**
     * <p>
     * Restricts the provided entity as specified by the provided {@link RestrictionCriteria}, overwriting any
     * previous restrictions.
     * </p>
     * <br>
     * <p>
     * If the provided entity is identifiable by {@link UUID}, the provided {@link RestrictionCriteria}
     * will be registered with said {@link UUID} rather than the provided entity instance.
     * </p>
     *
     * @param entity   The entity to restrict
     * @param criteria The {@link RestrictionCriteria} to restrict with
     */
    void put(Object entity, RestrictionCriteria criteria);

    /**
     * <p>
     * Restricts entities identified by the provided {@link UUID} as specified by the provided
     * {@link RestrictionCriteria}, overwriting any previous restrictions.
     * </p>
     *
     * @param uuid     The uuid of the entity to restrict
     * @param criteria The {@link RestrictionCriteria} to restrict with
     */
    void put(UUID uuid, RestrictionCriteria criteria);

    /**
     * <p>
     * Stops all restrictions for the provided entity.
     * </p>
     * <br>
     * <p>
     * If the provided entity is identifiable by {@link UUID}, removes all restrictions for entities identified by
     * said {@link UUID}.
     * </p>
     *
     * @param entity The entity to stop restrictions for
     * @return An {@link Optional} containing the removed {@link RestrictionCriteria} if it existed,
     * otherwise {@link Optional#empty()}
     */
    Optional<RestrictionCriteria> remove(Object entity);

    /**
     * <p>
     * Stops all restrictions for entities identified by the provided {@link UUID}.
     * </p>
     *
     * @param uuid The uuid of the entity to stop restricting
     * @return An {@link Optional} containing the removed {@link RestrictionCriteria} if it existed,
     * otherwise {@link Optional#empty()}
     */
    Optional<RestrictionCriteria> remove(UUID uuid);

    /**
     * <p>
     * Checks whether the provided entity is restricted.
     * </p>
     *
     * @param entity The entity to check
     * @return The {@link RestrictionCriteria} associated with the provided entity, otherwise
     * {@link RestrictionCriteria#none()}
     */
    RestrictionCriteria get(Object entity);

    /**
     * <p>
     * Checks whether the entity identified by the provided {@link UUID} is restricted.
     * </p>
     *
     * @param uuid The {@link UUID} of the entity to check
     * @return The {@link RestrictionCriteria} associated with the provided {@link UUID}, otherwise
     * {@link RestrictionCriteria#none()}
     */
    RestrictionCriteria get(UUID uuid);
}
