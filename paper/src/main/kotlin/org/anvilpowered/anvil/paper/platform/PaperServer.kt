package org.anvilpowered.anvil.paper.platform

import net.kyori.adventure.audience.Audience
import org.anvilpowered.anvil.core.platform.Platform
import org.anvilpowered.anvil.core.platform.Server
import org.bukkit.Bukkit

object PaperServer : Server {
    override val platform: Platform
        get() = PaperPlatform
    override val systemSubject: Audience
        get() = Bukkit.getConsoleSender()
}
