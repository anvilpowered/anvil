package org.anvilpowered.anvil.api

import org.anvilpowered.anvil.domain.platform.Platform
import org.anvilpowered.anvil.domain.platform.PluginManager

/**
 * To create an instance of this interface, use the `AnvilApi.create` function.
 * This is available for each platform in the corresponding `anvil-core-game-<platform>` module.
 *
 * Generally, the method will look something like this:
 * ```kt
 * AnvilApi.create<<<platform>>>("my-plugin", ....)
 * ```
 *
 * For example, for Velocity:
 *
 * ```kt
 * AnvilApi.createVelocity("my-plugin", ....)
 * ```
 */
interface AnvilApi : LoggerScope {
    val platform: Platform

    val pluginManager: PluginManager

    companion object
}
