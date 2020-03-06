/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.anvil.api.misc;

import com.google.common.reflect.TypeToken;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.anvilpowered.anvil.api.component.Component;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.manager.annotation.MongoDBComponent;
import org.anvilpowered.anvil.api.manager.annotation.XodusComponent;

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
     *     new TypeToken<FooRepository<?, ?, ?, ?>>(getClass()) {
     *     },
     *     new TypeToken<FooRepository<?, Foo<?>, ?, ?>>(getClass()) {
     *     },
     *     new TypeToken<FooRepository<ObjectId, BanRule<ObjectId>, Datastore, MongoConfig>>(getClass()) {
     *     },
     *     new TypeToken<CommonMongoFooRepository<TMongoFoo>>(getClass()) { // final implementation
     *     },
     *     MongoDBComponent.class
     * );
     * }</pre>
     */
    <From1 extends Component<?, ?>,
        From2 extends Component<?, ?>,
        From3 extends From1,
        Target extends From1>
    void bind(
        TypeToken<From1> from1,
        TypeToken<From2> from2,
        TypeToken<From3> from3,
        TypeToken<Target> target,
        Class<? extends Annotation> componentAnnotation
    );

    <From1 extends Component<?, ?>,
        From2 extends From1,
        Target extends From1>
    void bind(
        TypeToken<From1> from1,
        TypeToken<From2> from2,
        TypeToken<Target> target,
        Class<? extends Annotation> componentAnnotation
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
     * Binds the {@link DataStoreContext} matching the provided annotation
     * <p>
     * Passing {@link MongoDBComponent} to this method is the same as invoking:
     * </p>
     * <pre>{@code
     * bind(new TypeLiteral<DataStoreContext<ObjectId, Datastore>>() {
     * }).to(MongoContext.class);
     * }</pre>
     * <p>
     * Annotations may be of the following types:
     * - {@link MongoDBComponent}
     * - {@link XodusComponent}
     * </p>
     *
     * @param componentAnnotations Annotations matching a {@link DataStoreContext}.
     * @throws IllegalArgumentException If any of the provided annotations do not
     *                                  match a {@link DataStoreContext}
     */
    void withContexts(Class<? extends Annotation>... componentAnnotations);

    /**
     * Binds the {@link DataStoreContext} matching the provided annotation
     * <p>
     * Passing {@link MongoDBComponent} to this method is the same as invoking:
     * </p>
     * <pre>{@code
     * bind(new TypeLiteral<DataStoreContext<ObjectId, Datastore>>() {
     * }).to(MongoContext.class);
     * }</pre>
     * <p>
     * Annotations may be of the following types:
     * - {@link MongoDBComponent}
     * - {@link XodusComponent}
     * </p>
     *
     * @param a1 Annotation matching a {@link DataStoreContext}.
     * @throws IllegalArgumentException If any of the provided annotations do not
     *                                  match a {@link DataStoreContext}
     */
    default void withContexts(
        Class<? extends Annotation> a1
    ) {
        withContexts((Class<? extends Annotation>[]) new Class<?>[]{a1});
    }

    /**
     * Binds the {@link DataStoreContext} matching the provided annotation
     * <p>
     * Passing {@link MongoDBComponent} to this method is the same as invoking:
     * </p>
     * <pre>{@code
     * bind(new TypeLiteral<DataStoreContext<ObjectId, Datastore>>() {
     * }).to(MongoContext.class);
     * }</pre>
     * <p>
     * Annotations may be of the following types:
     * - {@link MongoDBComponent}
     * - {@link XodusComponent}
     * </p>
     *
     * @param a1 Annotation matching a {@link DataStoreContext}.
     * @param a2 Annotation matching a {@link DataStoreContext}.
     * @throws IllegalArgumentException If any of the provided annotations do not
     *                                  match a {@link DataStoreContext}
     */
    default void withContexts(
        Class<? extends Annotation> a1,
        Class<? extends Annotation> a2
    ) {
        withContexts((Class<? extends Annotation>[]) new Class<?>[]{a1, a2});
    }

    /**
     * Binds the {@link DataStoreContext} matching the provided annotation
     * <p>
     * Passing {@link MongoDBComponent} to this method is the same as invoking:
     * </p>
     * <pre>{@code
     * bind(new TypeLiteral<DataStoreContext<ObjectId, Datastore>>() {
     * }).to(MongoContext.class);
     * }</pre>
     * <p>
     * Annotations may be of the following types:
     * - {@link MongoDBComponent}
     * - {@link XodusComponent}
     * </p>
     *
     * @param a1 Annotation matching a {@link DataStoreContext}.
     * @param a2 Annotation matching a {@link DataStoreContext}.
     * @param a3 Annotation matching a {@link DataStoreContext}.
     * @throws IllegalArgumentException If any of the provided annotations do not
     *                                  match a {@link DataStoreContext}
     */
    default void withContexts(
        Class<? extends Annotation> a1,
        Class<? extends Annotation> a2,
        Class<? extends Annotation> a3
    ) {
        withContexts((Class<? extends Annotation>[]) new Class<?>[]{a1, a2, a3});
    }

    static <T> TypeLiteral<T> getTypeLiteral(TypeToken<T> typeToken) {
        return (TypeLiteral<T>) TypeLiteral.get(typeToken.getType());
    }

    static <T> Key<T> getKey(TypeToken<T> typeToken) {
        return Key.get(getTypeLiteral(typeToken));
    }
}
