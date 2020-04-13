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

package org.anvilpowered.anvil.common.module;

import com.google.common.reflect.TypeToken;
import com.google.inject.name.Names;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import org.anvilpowered.anvil.api.core.coremember.CoreMemberManager;
import org.anvilpowered.anvil.api.core.coremember.repository.CoreMemberRepository;
import org.anvilpowered.anvil.api.core.plugin.PluginMessages;
import org.anvilpowered.anvil.api.data.config.ConfigurationService;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.anvilpowered.anvil.api.plugin.BasicPluginInfo;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.base.data.registry.BaseExtendedRegistry;
import org.anvilpowered.anvil.common.coremember.CommonCoreMemberManager;
import org.anvilpowered.anvil.common.coremember.repository.CommonMongoCoreMemberRepository;
import org.anvilpowered.anvil.common.coremember.repository.CommonXodusCoreMemberRepository;
import org.anvilpowered.anvil.common.data.config.CommonConfigurationService;
import org.anvilpowered.anvil.common.misc.CommonBindingExtensions;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginInfo;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginMessges;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

@SuppressWarnings("UnstableApiUsage")
public class CommonModule<TString, TCommandSource> extends ApiCommonModule {

    @Override
    protected void configure() {

        BindingExtensions be = new CommonBindingExtensions(binder());

        be.bind(new TypeToken<PluginInfo<TString>>(getClass()) {
        }, new TypeToken<AnvilCorePluginInfo<TString, TCommandSource>>(getClass()) {
        });

        be.bind(new TypeToken<BasicPluginInfo>(getClass()) {
        }, new TypeToken<AnvilCorePluginInfo<TString, TCommandSource>>(getClass()) {
        });

        be.bind(new TypeToken<PluginMessages<TString>>(getClass()) {
        }, new TypeToken<AnvilCorePluginMessges<TString, TCommandSource>>(getClass()) {
        });

        be.bind(
            new TypeToken<CoreMemberRepository<?, ?>>() {
            },
            new TypeToken<CoreMemberRepository<ObjectId, Datastore>>() {
            },
            new TypeToken<CommonMongoCoreMemberRepository>() {
            },
            Names.named("mongodb")
        );

        be.bind(
            new TypeToken<CoreMemberRepository<?, ?>>() {
            },
            new TypeToken<CoreMemberRepository<EntityId, PersistentEntityStore>>() {
            },
            new TypeToken<CommonXodusCoreMemberRepository>() {
            },
            Names.named("xodus")
        );

        bind(CoreMemberManager.class).to(CommonCoreMemberManager.class);

        be.withContexts("mongodb", "xodus");

        bind(Registry.class).to(BaseExtendedRegistry.class);

        bind(ConfigurationService.class).to(CommonConfigurationService.class);
    }
}
