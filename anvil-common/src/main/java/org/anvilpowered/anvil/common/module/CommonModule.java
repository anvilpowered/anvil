/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
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
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import dev.morphia.Datastore;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import org.anvilpowered.anvil.api.coremember.CoreMemberManager;
import org.anvilpowered.anvil.api.coremember.CoreMemberRepository;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.anvilpowered.anvil.api.plugin.BasicPluginInfo;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.plugin.PluginMessages;
import org.anvilpowered.anvil.api.registry.ConfigurationService;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.base.registry.BaseExtendedRegistry;
import org.anvilpowered.anvil.common.anvilnet.listener.BaseListeners;
import org.anvilpowered.anvil.common.coremember.CommonCoreMemberManager;
import org.anvilpowered.anvil.common.coremember.CommonMongoCoreMemberRepository;
import org.anvilpowered.anvil.common.coremember.CommonXodusCoreMemberRepository;
import org.anvilpowered.anvil.common.misc.CommonBindingExtensions;
import org.anvilpowered.anvil.common.plugin.AnvilPluginInfo;
import org.anvilpowered.anvil.common.plugin.AnvilPluginMessages;
import org.anvilpowered.anvil.common.registry.CommonConfigurationService;
import org.bson.types.ObjectId;

@SuppressWarnings("UnstableApiUsage")
public class CommonModule<TString, TCommandSource> extends ApiCommonModule {

    @Override
    protected void configure() {

        BindingExtensions be = new CommonBindingExtensions(binder());

        be.bind(new TypeToken<PluginInfo<TString>>(getClass()) {
        }, new TypeToken<AnvilPluginInfo<TString, TCommandSource>>(getClass()) {
        });

        be.bind(new TypeToken<BasicPluginInfo>(getClass()) {
        }, new TypeToken<AnvilPluginInfo<TString, TCommandSource>>(getClass()) {
        });

        be.bind(new TypeToken<PluginMessages<TString>>(getClass()) {
        }, new TypeToken<AnvilPluginMessages<TString, TCommandSource>>(getClass()) {
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

        be.withMongoDB();
        be.withXodus();

        bind(Registry.class).to(BaseExtendedRegistry.class);

        bind(ConfigurationService.class).to(CommonConfigurationService.class);

        install(new FactoryModuleBuilder()
            .implement(BaseListeners.class, BaseListeners.class)
            .build(BaseListeners.Factory.class));
    }
}
