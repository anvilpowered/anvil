package org.anvilpowered.anvil.domain.platform

interface GamePlatform : Platform {
    val isProxy: Boolean
    val plugins: List<Plugin>
}
