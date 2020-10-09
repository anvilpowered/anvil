/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.common.module

import com.google.common.reflect.TypeToken
import com.google.inject.AbstractModule
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.plugin.BasicPluginInfo
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.base.registry.BaseRegistry
import org.anvilpowered.anvil.common.plugin.FallbackPluginInfo

@Suppress("UnstableApiUsage")
open class FallbackModule<TString : Any, TCommandSource> : AbstractModule() {
    override fun configure() {
        val be = Anvil.getBindingExtensions(binder());
        val fallbackPluginInfoToken = object : TypeToken<FallbackPluginInfo<TString, TCommandSource>>(javaClass) {}
        be.bind(TypeToken.of(BasicPluginInfo::class.java), fallbackPluginInfoToken)
        be.bind(object : TypeToken<PluginInfo<TString>>(javaClass) {}, fallbackPluginInfoToken)
        bind(Registry::class.java).to(BaseRegistry::class.java)
    }
}
