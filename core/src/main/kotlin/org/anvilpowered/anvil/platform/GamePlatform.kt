package org.anvilpowered.anvil.platform

interface GamePlatform : Platform {
    val isProxy: Boolean
    val plugins: List<Plugin>
}
