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
package org.anvilpowered.anvil.core.module

import com.google.inject.TypeLiteral
import com.google.inject.name.Names
import dev.morphia.Datastore
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import org.anvilpowered.anvil.api.coremember.CoreMemberManager
import org.anvilpowered.anvil.api.coremember.CoreMemberRepository
import org.anvilpowered.anvil.api.misc.bind
import org.anvilpowered.anvil.api.misc.to
import org.anvilpowered.anvil.api.misc.withMongoDB
import org.anvilpowered.anvil.api.misc.withXodus
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.core.coremember.CommonCoreMemberManager
import org.anvilpowered.anvil.core.coremember.CommonMongoCoreMemberRepository
import org.anvilpowered.anvil.core.coremember.CommonXodusCoreMemberRepository
import org.anvilpowered.anvil.core.plugin.AnvilPluginInfo
import org.anvilpowered.anvil.core.registry.CommonConfigurationService
import org.anvilpowered.registry.api.ConfigurationService
import org.anvilpowered.registry.api.Registry
import org.bson.types.ObjectId
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.loader.ConfigurationLoader
import java.nio.file.Paths

@Suppress("UnstableApiUsage")
abstract class CommonModule<TCommandSource>(private val configDir: String) : ApiCommonModule() {

    override fun configure() {

        val configDirFull = Paths.get("$configDir/anvil").toFile()
        if (!configDirFull.exists()) {
            check(configDirFull.mkdirs()) { "Unable to create config directory" }
        }
        bind(object : TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {}).toInstance(
            HoconConfigurationLoader.builder().path(Paths.get("$configDirFull/anvil.conf")).build()
        )

        with(binder()) {
            bind<CoreMemberManager>().to<CommonCoreMemberManager>()
            bind<PluginInfo>().to<AnvilPluginInfo>()
            bind<CoreMemberRepository<*, *>>()
                .annotatedWith(Names.named("mongodb"))
                .to<CommonMongoCoreMemberRepository>()
            bind<CoreMemberRepository<ObjectId, Datastore>>()
                .to<CommonMongoCoreMemberRepository>()
            bind<CoreMemberRepository<*, *>>()
                .annotatedWith(Names.named("xodus"))
                .to<CommonXodusCoreMemberRepository>()
            bind<CoreMemberRepository<EntityId, PersistentEntityStore>>()
                .to<CommonXodusCoreMemberRepository>()


            withMongoDB()
            withXodus()
        }
        bind(Registry::class.java).to(ConfigurationService::class.java)
        bind(ConfigurationService::class.java).to(CommonConfigurationService::class.java)
    }
}
