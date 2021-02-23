/*
 * Anvil - AnvilPowered
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package org.anvilpowered.anvil.common.module

import com.google.common.reflect.TypeToken
import com.google.inject.TypeLiteral
import com.google.inject.name.Names
import dev.morphia.Datastore
import java.nio.file.Paths
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.anvilpowered.anvil.api.coremember.CoreMemberManager
import org.anvilpowered.anvil.api.coremember.CoreMemberRepository
import org.anvilpowered.anvil.api.plugin.BasicPluginInfo
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.plugin.PluginMessages
import org.anvilpowered.anvil.api.registry.ConfigurationService
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.base.registry.BaseExtendedRegistry
import org.anvilpowered.anvil.common.coremember.CommonCoreMemberManager
import org.anvilpowered.anvil.common.coremember.CommonMongoCoreMemberRepository
import org.anvilpowered.anvil.common.coremember.CommonXodusCoreMemberRepository
import org.anvilpowered.anvil.common.misc.CommonBindingExtensions
import org.anvilpowered.anvil.common.plugin.AnvilPluginInfo
import org.anvilpowered.anvil.common.plugin.AnvilPluginMessages
import org.anvilpowered.anvil.common.registry.CommonConfigurationService
import org.bson.types.ObjectId

@Suppress("UnstableApiUsage")
open class CommonModule<TString, TCommandSource>(private val configDir: String) : ApiCommonModule() {

  override fun configure() {
    val be = CommonBindingExtensions(binder())

    be.bind(
      object : TypeToken<PluginInfo<TString>>(javaClass) {},
      object : TypeToken<AnvilPluginInfo<TString, TCommandSource>>(javaClass) {}
    )
    be.bind(
      object : TypeToken<BasicPluginInfo>(javaClass) {},
      object : TypeToken<AnvilPluginInfo<TString, TCommandSource>>(javaClass) {}
    )
    be.bind(
      object : TypeToken<PluginMessages<TString>>(javaClass) {},
      object : TypeToken<AnvilPluginMessages<TString, TCommandSource>>(javaClass) {}
    )

    be.bind(
      object : TypeToken<CoreMemberRepository<*, *>>() {},
      object : TypeToken<CoreMemberRepository<ObjectId, Datastore>>() {},
      object : TypeToken<CommonMongoCoreMemberRepository>() {},
      Names.named("mongodb")
    )
    be.bind(
      object : TypeToken<CoreMemberRepository<*, *>>() {},
      object : TypeToken<CoreMemberRepository<EntityId, PersistentEntityStore>>() {},
      object : TypeToken<CommonXodusCoreMemberRepository>() {},
      Names.named("xodus")
    )

    bind(CoreMemberManager::class.java).to(CommonCoreMemberManager::class.java)

    be.withMongoDB()
    be.withXodus()

    bind(Registry::class.java).to(BaseExtendedRegistry::class.java)
    bind(ConfigurationService::class.java).to(CommonConfigurationService::class.java)

    val configDirFull = Paths.get("$configDir/anvil").toFile()
    if (!configDirFull.exists()) {
      check(configDirFull.mkdirs()) { "Unable to create config directory" }
    }
    bind(object : TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {}).toInstance(
      HoconConfigurationLoader.builder().setPath(Paths.get("$configDirFull/anvil.conf")).build()
    )
  }
}
