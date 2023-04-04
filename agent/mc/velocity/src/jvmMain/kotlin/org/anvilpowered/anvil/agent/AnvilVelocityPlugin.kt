package org.anvilpowered.anvil.agent

import com.google.inject.Inject
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.RegisteredServer
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import kotlin.jvm.optionals.getOrNull

@Plugin(
    id = "anvil-agent",
    name = "Anvil Agent",
    version = "0.4.0-SNAPSHOT",
    authors = ["AnvilPowered"],
)
class AnvilVelocityPlugin @Inject constructor(
    private val proxyServer: ProxyServer,
    private val logger: Logger,
) {
    init {
        logger.info("Hello, world from anvil-agent!")
    }
}
