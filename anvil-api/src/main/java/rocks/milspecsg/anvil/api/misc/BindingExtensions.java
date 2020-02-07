/*
 *   Anvil - MilSpecSG
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

package rocks.milspecsg.anvil.api.misc;

import com.google.common.reflect.TypeToken;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import rocks.milspecsg.anvil.api.component.Component;

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

    static <T> TypeLiteral<T> getTypeLiteral(TypeToken<T> typeToken) {
        return (TypeLiteral<T>) TypeLiteral.get(typeToken.getType());
    }

    static <T> Key<T> getKey(TypeToken<T> typeToken) {
        return Key.get(getTypeLiteral(typeToken));
    }
}
