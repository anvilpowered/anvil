package org.anvilpowered.anvil.datastore

expect class UUID

expect object Crypto {
    fun randomUUID(): UUID
}
