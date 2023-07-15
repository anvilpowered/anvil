package org.anvilpowered.anvil.platform

import org.anvilpowered.anvil.domain.platform.GamePlatform
import org.anvilpowered.anvil.domain.platform.Plugin
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
