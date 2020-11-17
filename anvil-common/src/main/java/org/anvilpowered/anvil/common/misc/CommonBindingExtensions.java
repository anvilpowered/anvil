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

package org.anvilpowered.anvil.common.misc;

import com.google.common.reflect.TypeToken;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import dev.morphia.Datastore;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import org.anvilpowered.anvil.api.datastore.Component;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.datastore.MongoContext;
import org.anvilpowered.anvil.api.datastore.XodusContext;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.bson.types.ObjectId;

import java.lang.annotation.Annotation;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class CommonBindingExtensions implements BindingExtensions {

    private final Binder binder;

    public CommonBindingExtensions(Binder binder) {
        this.binder = binder;
    }

    @Override
    public <From1 extends Component<?, ?>,
        From2 extends Component<?, ?>,
        From3 extends From1,
        Target extends From1>
    void bind(
        TypeToken<From1> from1,
        TypeToken<From2> from2,
        TypeToken<From3> from3,
        TypeToken<Target> target,
        Annotation componentAnnotation
    ) {
        binder.bind((TypeLiteral<From1>) TypeLiteral.get(from2.getType()))
            .annotatedWith(componentAnnotation)
            .to(BindingExtensions.getTypeLiteral(target));

        binder.bind((TypeLiteral<From1>) TypeLiteral.get(from3.getType()))
            .to(BindingExtensions.getTypeLiteral(target));
    }

    @Override
    public <From1 extends Component<?, ?>,
        From2 extends From1,
        Target extends From1>
    void bind(
        TypeToken<From1> from1,
        TypeToken<From2> from2,
        TypeToken<Target> target,
        Annotation componentAnnotation
    ) {
        bind(from1, from1, from2, target, componentAnnotation);
    }

    @Override
    public <From, Target extends From> void bind(
        TypeToken<From> from,
        TypeToken<Target> target,
        Annotation annotation
    ) {
        binder.bind(BindingExtensions.getTypeLiteral(from))
            .annotatedWith(annotation)
            .to(BindingExtensions.getTypeLiteral(target));
    }

    @Override
    public <From, Target extends From> void bind(
        TypeToken<From> from,
        TypeToken<Target> target,
        Class<? extends Annotation> annotation
    ) {
        binder.bind(BindingExtensions.getTypeLiteral(from))
            .annotatedWith(annotation)
            .to(BindingExtensions.getTypeLiteral(target));
    }

    @Override
    public <From, Target extends From> void bind(
        TypeToken<From> from,
        TypeToken<Target> target
    ) {
        binder.bind(BindingExtensions.getTypeLiteral(from))
            .to(BindingExtensions.getTypeLiteral(target));
    }

    @Override
    public void withMongoDB() {
        binder.bind(new TypeLiteral<DataStoreContext<ObjectId, Datastore>>() {
        }).to(MongoContext.class);
    }

    @Override
    public void withXodus() {
        binder.bind(new TypeLiteral<DataStoreContext<EntityId, PersistentEntityStore>>() {
        }).to(XodusContext.class);
    }
}
