package org.anvilpowered.anvil.datastore

actual typealias UUID = java.util.UUID

actual object Crypto {
    actual fun randomUUID(): UUID = UUID.randomUUID()
}

fun foo() {
}
