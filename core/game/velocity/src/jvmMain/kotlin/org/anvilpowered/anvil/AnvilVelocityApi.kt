package org.anvilpowered.anvil

import com.velocitypowered.api.proxy.ProxyServer
import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.core.Slf4jDelegateLogger
import org.anvilpowered.anvil.platform.VelocityPlatform
import org.anvilpowered.anvil.platform.VelocityPluginManager
import org.slf4j.Logger as VelocityLogger

interface AnvilVelocityApi : AnvilApi {
    val proxyServer: ProxyServer
}

fun AnvilApi.Companion.createVelocity(logger: VelocityLogger, proxyServer: ProxyServer): AnvilVelocityApi {
    return object : AnvilVelocityApi {
        override val logger = Slf4jDelegateLogger(logger)
        override val platform = VelocityPlatform(proxyServer)
        override val pluginManager = VelocityPluginManager(proxyServer.pluginManager)
        override val proxyServer: ProxyServer = proxyServer
    }
}
