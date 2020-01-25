/*
 *   MSRepository - MilSpecSG
 *   Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.mscore.common.module;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import rocks.milspecsg.mscore.api.coremember.CoreMemberManager;
import rocks.milspecsg.mscore.api.coremember.repository.CoreMemberRepository;
import rocks.milspecsg.mscore.api.plugin.PluginMessages;
import rocks.milspecsg.mscore.common.coremember.CommonCoreMemberManager;
import rocks.milspecsg.mscore.common.coremember.repository.CommonMongoCoreMemberRepository;
import rocks.milspecsg.mscore.common.coremember.repository.CommonXodusCoreMemberRepository;
import rocks.milspecsg.mscore.common.data.config.MSCoreConfigurationService;
import rocks.milspecsg.mscore.common.plugin.MSCorePluginInfo;
import rocks.milspecsg.mscore.common.plugin.MSCorePluginMessages;
import rocks.milspecsg.msrepository.api.data.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.api.datastore.MongoContext;
import rocks.milspecsg.msrepository.api.datastore.XodusContext;
import rocks.milspecsg.msrepository.api.manager.annotation.MongoDBComponent;
import rocks.milspecsg.msrepository.api.manager.annotation.XodusComponent;
import rocks.milspecsg.msrepository.api.misc.BindingExtensions;
import rocks.milspecsg.msrepository.api.util.BasicPluginInfo;
import rocks.milspecsg.msrepository.api.util.PluginInfo;
import rocks.milspecsg.msrepository.common.data.registry.CommonExtendedRegistry;
import rocks.milspecsg.msrepository.common.misc.CommonBindingExtensions;
import rocks.milspecsg.msrepository.common.module.ApiCommonModule;

@SuppressWarnings("UnstableApiUsage")
public class CommonModule<TString, TCommandSource> extends ApiCommonModule {

    @Override
    protected void configure() {

        BindingExtensions be = new CommonBindingExtensions(binder());

        be.bind(new TypeToken<PluginInfo<TString>>(getClass()) {
        }, new TypeToken<MSCorePluginInfo<TString, TCommandSource>>(getClass()) {
        });

        be.bind(new TypeToken<BasicPluginInfo>(getClass()) {
        }, new TypeToken<MSCorePluginInfo<TString, TCommandSource>>(getClass()) {
        });

        be.bind(new TypeToken<PluginMessages<TString>>(getClass()) {
        }, new TypeToken<MSCorePluginMessages<TString, TCommandSource>>(getClass()) {
        });

        be.bind(
            new TypeToken<CoreMemberRepository<?, ?>>() {
            },
            new TypeToken<CoreMemberRepository<ObjectId, Datastore>>() {
            },
            new TypeToken<CommonMongoCoreMemberRepository>() {
            },
            MongoDBComponent.class
        );

        be.bind(
            new TypeToken<CoreMemberRepository<?, ?>>() {
            },
            new TypeToken<CoreMemberRepository<EntityId, PersistentEntityStore>>() {
            },
            new TypeToken<CommonXodusCoreMemberRepository>() {
            },
            XodusComponent.class
        );

        bind(CoreMemberManager.class).to(CommonCoreMemberManager.class);

        bind(new TypeLiteral<DataStoreContext<ObjectId, Datastore>>() {
        }).to(MongoContext.class);

        bind(new TypeLiteral<DataStoreContext<EntityId, PersistentEntityStore>>() {
        }).to(XodusContext.class);

        bind(Registry.class).to(CommonExtendedRegistry.class);

        bind(ConfigurationService.class).to(MSCoreConfigurationService.class);
    }
}
