/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil

import org.anvilpowered.anvil.platform.Platform
import org.anvilpowered.anvil.platform.PluginManager

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
interface AnvilApi : LoggerScope, RepositoryScope {
    val platform: Platform

    val pluginManager: PluginManager

    companion object
}
