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
package org.anvilpowered.anvil.base.datastore

import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import org.anvilpowered.anvil.api.datastore.DBComponent
import java.util.Optional

interface BaseXodusComponent : DBComponent<EntityId, PersistentEntityStore> {

    override fun parseUnsafe(`object`: Any): EntityId {
        if (`object` is EntityId) {
            return `object`
        } else if (`object` is Optional<*>) {
            val optional = `object`
            if (optional.isPresent) return parseUnsafe(optional.get())
            throw IllegalArgumentException("Error while parsing $`object`. Optional not present")
        }
        val string = `object`.toString()
        val stringParts = string.split("-").toTypedArray()
        require(stringParts.size == 2) { "Not a valid EntityId. Must follow format (int)-(long)" }
        return PersistentEntityId(stringParts[0].toInt(), stringParts[1].toLong())
    }
}
