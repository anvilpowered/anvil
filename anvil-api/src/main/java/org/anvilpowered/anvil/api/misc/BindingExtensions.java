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

package org.anvilpowered.anvil.api.misc;

import com.google.common.reflect.TypeToken;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.datastore.DBComponent;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;

import java.lang.annotation.Annotation;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public interface BindingExtensions {

    /**
     * Full binding method for a component
     * <p>
     * A typical example of usage of this method:
     * </p>
     * <pre>{@code
     * be.bind(
     *     new TypeToken<FooRepository<?, ?, ?>>(getClass()) {
     *     },
     *     new TypeToken<FooRepository<?, Foo<?, ?>>(getClass()) {
     *     },
     *     new TypeToken<FooRepository<ObjectId, Foo<ObjectId>, Datastore>>(getClass()) {
     *     },
     *     new TypeToken<CommonMongoFooRepository<TMongoFoo>>(getClass()) { // final implementation
     *     },
     *     Names.named("mongodb")
     * );
     * }</pre>
     */
    <From1 extends DBComponent<?, ?>,
        From2 extends DBComponent<?, ?>,
        From3 extends From1,
        Target extends From1>
    void bind(
        TypeToken<From1> from1,
        TypeToken<From2> from2,
        TypeToken<From3> from3,
        TypeToken<Target> target,
        Annotation componentAnnotation
    );

    /**
     * Binding method for a component
     * <p>
     * A typical example of usage of this method:
     * </p>
     * <pre>{@code
     * be.bind(
     *     new TypeToken<FooRepository<?, ?>>(getClass()) {
     *     },
     *     new TypeToken<FooRepository<ObjectId, Datastore>>(getClass()) {
     *     },
     *     new TypeToken<CommonMongoFooRepository>(getClass()) { // final implementation
     *     },
     *     Names.named("mongodb")
     * );
     * }</pre>
     */
    <From1 extends DBComponent<?, ?>,
        From2 extends From1,
        Target extends From1>
    void bind(
        TypeToken<From1> from1,
        TypeToken<From2> from2,
        TypeToken<Target> target,
        Annotation componentAnnotation
    );

    <From, Target extends From> void bind(
        TypeToken<From> from,
        TypeToken<Target> target,
        Annotation annotation
    );

    <From, Target extends From> void bind(
        TypeToken<From> from,
        TypeToken<Target> target,
        Class<? extends Annotation> annotation
    );

    <From, Target extends From> void bind(
        TypeToken<From> from,
        TypeToken<Target> target
    );

    /**
     * Binds the mongodb {@link DataStoreContext}
     * <p>
     * Using this method is the same as invoking:
     * </p>
     * <pre>{@code
     * bind(new TypeLiteral<DataStoreContext<ObjectId, Datastore>>() {
     * }).to(MongoContext.class);
     * }</pre>
     */
    void withMongoDB();

    /**
     * Binds the xodus {@link DataStoreContext}
     * <p>
     * Using this method is the same as invoking:
     * </p>
     * <pre>{@code
     * binder.bind(new TypeLiteral<DataStoreContext<EntityId, PersistentEntityStore>>() {
     * }).to(XodusContext.class);
     * }</pre>
     */
    void withXodus();


    static <T> TypeLiteral<T> getTypeLiteral(TypeToken<T> typeToken) {
        return (TypeLiteral<T>) TypeLiteral.get(typeToken.getType());
    }

    static <T> Key<T> getKey(TypeToken<T> typeToken) {
        return Key.get(getTypeLiteral(typeToken));
    }

    static <T> Provider<T> asInternalProvider(Class<T> clazz) {
        return () -> Anvil.getEnvironment().getInjector()
            .getInstance(clazz);
    }

    static <T> Provider<T> asInternalProvider(TypeLiteral<T> typeLiteral) {
        return () -> Anvil.getEnvironment().getInjector()
            .getInstance(Key.get(typeLiteral));
    }

    static <T> Provider<T> asInternalProvider(TypeToken<T> typeToken) {
        return () -> Anvil.getEnvironment().getInjector()
            .getInstance(BindingExtensions.getKey(typeToken));
    }
}
