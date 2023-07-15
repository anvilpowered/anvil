package org.anvilpowered.anvil

import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.core.Slf4jDelegateLogger
import org.anvilpowered.anvil.db.RepositoryScopeImpl
import org.anvilpowered.anvil.domain.RepositoryScope
import org.anvilpowered.anvil.platform.SpongePlatform
import org.anvilpowered.anvil.platform.SpongePluginManager
import org.slf4j.Logger

interface AnvilSpongeApi : AnvilApi {

    companion object
}


/**
 * Creates an Anvil API instance for Sponge.
 *
 * The returned API instance can be used to access the Anvil API.
 * It is intended to be used as a context receiver.
 *
 * For example, you can access it in a class like this:
 * ```kt
 * context(AnvilSpongeApi) // or AnvilApi if in common code
 * class MyPlugin {
 *     fun foo() {
 *         logger.info { "Hello, world!" } // logger property of [AnvilApi] is accessed through context receiver
 *     }
 * }
 * ```
 *
 * In order to invoke this constructor, you must have an instance of [AnvilSpongeApi] in the calling context.
 * To bring an instance of [AnvilSpongeApi] into the calling context, you can use the [with] function:
 *
 * ```kt
 * private val plugin = with(AnvilApi.createSponge(logger, proxyServer)) {
 *     AnvilSpongePlugin()
 * }
 * ```
 *
 * Context receivers may also be used on individual functions.
 * This is particularly useful for top-level functions:
 *
 * ```kt
 * context(AnvilSpongeApi) // or AnvilApi if in common code
 * fun foo() {
 *     logger.info { "Hello, world!" } // logger property of [AnvilApi] is accessed through context receiver
 * }
 * ```
 */
fun AnvilApi.Companion.createSponge(logger: Logger): AnvilSpongeApi {
    return object : AnvilSpongeApi, RepositoryScope by RepositoryScopeImpl {
        override val logger = Slf4jDelegateLogger(logger)
        override val platform = SpongePlatform
        override val pluginManager = SpongePluginManager
    }
}
