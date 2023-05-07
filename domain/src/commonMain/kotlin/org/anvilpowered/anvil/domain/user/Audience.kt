package org.anvilpowered.anvil.domain.user

expect interface Audience {
    open fun sendMessage(message: Component)
}
