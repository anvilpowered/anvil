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
package org.anvilpowered.anvil.api.datastore

import com.google.inject.Inject
import com.google.inject.Singleton
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.anvilpowered.anvil.api.model.Mappable
import org.anvilpowered.anvil.api.registry.AnvilKeys
import org.anvilpowered.registry.Registry
import java.nio.file.Paths

@Singleton
class XodusContext @Inject constructor(registry: Registry) : DataStoreContext<EntityId, PersistentEntityStore>(registry) {
    override fun closeConnection(dataStore: PersistentEntityStore) {
        dataStore.close()
    }

    override fun loadDataStore(): PersistentEntityStore {

        /* === Initialize storage location === */
        val dbFilesLocation = Paths.get(registry.getOrDefault(AnvilKeys.DATA_DIRECTORY) + "/data/xodus").toFile()
        if (!dbFilesLocation.exists()) {
            check(dbFilesLocation.mkdirs()) { "Unable to create xodus directory" }
        }

        /* === Find objects to map === */
        val entityClasses = calculateEntityClasses(registry.getOrDefault(AnvilKeys.BASE_SCAN_PACKAGE),
            XodusEntity::class.java,
            XodusEmbedded::class.java)

        /* === Create collections if not present === */
        for (entityClass in entityClasses) {
            if (Mappable::class.java.isAssignableFrom(entityClass)) {
                try {
                    entityClass.getDeclaredMethod("writeTo", Entity::class.java)
                    entityClass.getDeclaredMethod("readFrom", Entity::class.java)
                } catch (e: NoSuchMethodException) {
                    throw IllegalStateException("Xodus entity class " + entityClass.name + " must implement Mappable#writeTo(T) and Mappable#readFrom(T)",
                        e)
                }
            } else check(!entityClass.isAnnotationPresent(XodusEntity::class.java)) {
                "Xodus entity class " + entityClass.name + " must extend org.anvilpowered.anvil.api.model.Mappable"
            }
        }
        tKeyClass = EntityId::class.java
        return PersistentEntityStores.newInstance(dbFilesLocation.path)
    }
}
