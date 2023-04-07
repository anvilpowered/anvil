package org.anvilpowered.anvil.datastore

import org.sourcegrade.kontour.UUID

interface GameTypeJoin<R> {
    suspend fun id(id: UUID): R
    suspend fun name(name: String): R
    suspend fun website(website: String): R
}
