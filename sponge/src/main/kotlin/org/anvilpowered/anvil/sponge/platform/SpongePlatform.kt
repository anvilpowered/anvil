package org.anvilpowered.anvil.sponge.platform

import org.anvilpowered.anvil.core.platform.Platform
import org.anvilpowered.anvil.core.platform.Plugin
import org.spongepowered.api.Sponge
import org.spongepowered.api.Platform as SPlatform

internal object SpongePlatform : Platform {
    override val isProxy: Boolean = false
    override val plugins: List<Plugin>
        get() = Sponge.pluginManager().plugins().map { it.toAnvilPlugin() }
    override val name: String
        get() = "sponge"
    override val version: String
        get() = Sponge.platform().container(SPlatform.Component.IMPLEMENTATION).metadata().version().qualifier
}
