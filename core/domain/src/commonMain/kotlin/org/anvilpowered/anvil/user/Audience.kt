package org.anvilpowered.anvil.user

expect interface Audience {
    open fun sendMessage(message: Component)
}
