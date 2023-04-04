package org.anvilpowered.anvil.datastore

actual typealias UUID = String

@JsName("crypto")
actual external object Crypto {
    actual fun randomUUID(): UUID
}

