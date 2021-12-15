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

import dev.morphia.Datastore
import org.anvilpowered.anvil.api.datastore.DBComponent
import org.bson.types.ObjectId
import java.util.Optional

interface BaseMongoComponent : DBComponent<ObjectId, Datastore> {
    override fun parseUnsafe(obj: Any): ObjectId {
        if (obj is ObjectId) {
            return obj
        } else if (obj is Optional<*>) {
            if (obj.isPresent) return parseUnsafe(obj.get())
            throw IllegalArgumentException("Error while parsing $obj. Optional not present")
        }
        val string = obj.toString()
        if (ObjectId.isValid(string)) return ObjectId(string)
        throw IllegalArgumentException("Error while parsing $obj. Not a valid ObjectId")
    }
}
