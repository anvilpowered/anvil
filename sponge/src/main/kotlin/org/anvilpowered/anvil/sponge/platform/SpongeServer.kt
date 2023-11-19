package org.anvilpowered.anvil.sponge.platform

import net.kyori.adventure.audience.Audience
import org.anvilpowered.anvil.core.platform.Platform
import org.anvilpowered.anvil.core.platform.Server
import org.spongepowered.api.Sponge

object SpongeServer : Server {
    override val platform: Platform
        get() = SpongePlatform
    override val broadcastAudience: Audience
        get() = Sponge.server().broadcastAudience()
    override val systemSubject: Audience
        get() = Sponge.game().systemSubject()
}
