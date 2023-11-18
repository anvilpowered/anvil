package org.anvilpowered.anvil.velocity.platform

import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.audience.Audience
import org.anvilpowered.anvil.core.platform.Platform
import org.anvilpowered.anvil.core.platform.Server

class VelocityServer(proxyServer: ProxyServer) : Server {
    override val platform: Platform = VelocityPlatform(proxyServer)
    override val systemSubject: Audience = proxyServer.consoleCommandSource
}
