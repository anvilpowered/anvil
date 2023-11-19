package org.anvilpowered.anvil.paper.platform

import net.kyori.adventure.audience.Audience
import org.anvilpowered.anvil.core.platform.Platform
import org.anvilpowered.anvil.core.platform.Server
import org.bukkit.Bukkit

object PaperServer : Server {
    override val platform: Platform
        get() = PaperPlatform
    override val broadcastAudience: Audience
        get() = Bukkit.getServer()
    override val systemSubject: Audience
        get() = Bukkit.getConsoleSender()
}
