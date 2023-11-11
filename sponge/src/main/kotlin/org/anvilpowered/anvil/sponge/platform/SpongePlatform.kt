package org.anvilpowered.anvil.sponge.platform

import org.anvilpowered.anvil.core.platform.GamePlatform
import org.anvilpowered.anvil.core.platform.Plugin
import org.spongepowered.api.Platform
import org.spongepowered.api.Sponge

internal object SpongePlatform : GamePlatform {
    override val isProxy: Boolean = false
    override val plugins: List<Plugin>
        get() = Sponge.pluginManager().plugins().map { it.toAnvilPlugin() }
    override val name: String
        get() = "sponge"
    override val platformVersion: String
        get() = Sponge.platform().container(Platform.Component.IMPLEMENTATION).metadata().version().qualifier
}
