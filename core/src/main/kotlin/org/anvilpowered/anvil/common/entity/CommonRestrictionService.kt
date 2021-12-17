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
package org.anvilpowered.anvil.common.entity

import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.entity.RestrictionCriteria.Companion.none
import org.anvilpowered.anvil.api.entity.RestrictionService
import org.anvilpowered.anvil.api.entity.RestrictionCriteria
import java.util.HashMap
import java.util.Optional
import java.util.UUID

@Singleton
class CommonRestrictionService : RestrictionService {

    @Inject
    private lateinit var entityUtils: EntityUtils

    private val entityRestrictions: MutableMap<Any, RestrictionCriteria>
    private val uuidRestrictions: MutableMap<UUID, RestrictionCriteria>

    override fun put(entity: Any, criteria: RestrictionCriteria) {
        val uuid = entityUtils.extractUUID(entity)
        if (uuid != null) {
            // register with UUID instead, we don't want to persist the instances of identifiable entities
            put(uuid, criteria)
            return
        }
        if (criteria == none()) {
            entityRestrictions.remove(entity)
        }
        entityRestrictions[entity] = criteria
    }

    override fun put(uuid: UUID, criteria: RestrictionCriteria) {
        if (criteria == none()) {
            uuidRestrictions.remove(uuid)
        }
        uuidRestrictions[uuid] = criteria
    }

    override fun remove(entity: Any): Optional<RestrictionCriteria> {
        val uuid = entityUtils.extractUUID(entity)
        return uuid?.let { remove(it) } ?: Optional.ofNullable(entityRestrictions.remove(entity))
    }

    override fun remove(uuid: UUID): Optional<RestrictionCriteria> {
        return Optional.ofNullable(uuidRestrictions.remove(uuid))
    }

    override fun get(entity: Any): RestrictionCriteria {
        val uuid = entityUtils.extractUUID(entity)
        return uuid?.let { get(it) } ?: entityRestrictions.getOrDefault(entity, none())
    }

    override fun get(uuid: UUID): RestrictionCriteria {
        return uuidRestrictions.getOrDefault(uuid, none())
    }

    init {
        entityRestrictions = HashMap()
        uuidRestrictions = HashMap()
    }
}