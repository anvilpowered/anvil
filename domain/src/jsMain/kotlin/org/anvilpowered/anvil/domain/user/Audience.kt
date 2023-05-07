package org.anvilpowered.anvil.domain.user

actual interface Audience {
    actual fun sendMessage(message: Component) {
    }
}
