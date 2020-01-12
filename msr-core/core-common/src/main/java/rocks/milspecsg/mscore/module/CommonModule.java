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

package rocks.milspecsg.mscore.module;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import rocks.milspecsg.mscore.api.coremember.CoreMemberManager;
import rocks.milspecsg.mscore.api.coremember.repository.CoreMemberRepository;
import rocks.milspecsg.mscore.service.common.coremember.CommonCoreMemberManager;
import rocks.milspecsg.mscore.service.common.coremember.repository.CommonMongoCoreMemberRepository;
import rocks.milspecsg.msrepository.BindingExtensions;
import rocks.milspecsg.msrepository.CommonBindingExtensions;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.manager.annotation.MongoDBComponent;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.datastore.mongodb.MongoContext;
import rocks.milspecsg.msrepository.service.common.config.CommonConfigurationService;
import rocks.milspecsg.msrepository.service.registry.CommonExtendedRegistry;

public class CommonModule extends AbstractModule {

    @Override
    @SuppressWarnings("UnstableApiUsage")
    protected void configure() {

        BindingExtensions be = new CommonBindingExtensions(binder());

        be.bind(
            new TypeToken<CoreMemberRepository<?, ?>>() {
            },
            new TypeToken<CoreMemberRepository<ObjectId, Datastore>>() {
            },
            new TypeToken<CommonMongoCoreMemberRepository>() {
            },
            MongoDBComponent.class
        );

        bind(CoreMemberManager.class).to(CommonCoreMemberManager.class);

        bind(new TypeLiteral<DataStoreContext<ObjectId, Datastore>>() {
        }).to(MongoContext.class);

        bind(Registry.class).to(CommonExtendedRegistry.class);

        bind(ConfigurationService.class).to(CommonConfigurationService.class);
    }
}
