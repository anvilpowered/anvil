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

package org.anvilpowered.anvil.api.misc

import com.google.common.reflect.TypeToken
import com.google.inject.Key
import com.google.inject.Provider
import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.datastore.DBComponent

@Suppress("unchecked", "UnstableApiUsage")
interface BindingExtensions {

  /**
   * Full binding method for a component
   *
   * A typical example of usage of this method:
   *
   * be.bind(
   *
   * &nbsp;&nbsp;new TypeToken<FooRepository<?, ?, ?>>(getClass()) {
   * },
   *
   * &nbsp;&nbsp;new TypeToken<FooRepository<?, Foo<?, ?>>(getClass()) {
   * },
   *
   * &nbsp;&nbsp;new TypeToken<FooRepository<ObjectId, Foo<ObjectId>, Datastore>>(getClass()) {
   * },
   *
   * &nbsp;&nbsp;new TypeToken<CommonMongoFooRepository<TMongoFoo>>(getClass()) { // final implementation
   * },
   *
   * &nbsp;&nbsp;Names.named("mongodb")
   *
   * );
   */
  fun <From1 : DBComponent<*, *>, From2 : DBComponent<*, *>, From3 : From1, Target : From1> bind(
    from1: TypeToken<From1>,
    from2: TypeToken<From2>,
    from3: TypeToken<From3>,
    target: TypeToken<Target>,
    componentAnnotation: Annotation,
  )

  /**
   * Binding method for a component
   *
   *
   * A typical example of usage of this method:
   *
   * be.bind(
   *
   * &nbsp;&nbsp;new TypeToken<FooRepository<?, ?>>(getClass()) {
   * },
   *
   * &nbsp;&nbsp;new TypeToken<FooRepository<ObjectId, Datastore>>(getClass()) {
   * },
   *
   * &nbsp;&nbsp;new TypeToken<CommonMongoFooRepository>(getClass()) { // final implementation
   * },
   * &nbsp;&nbsp;Names.named("mongodb")
   *
   * );
   */
  fun <From1 : DBComponent<*, *>, From2 : From1, Target : From1> bind(
    from1: TypeToken<From1>,
    from2: TypeToken<From2>,
    target: TypeToken<Target>,
    componentAnnotation: Annotation,
  )

  fun <From, Target : From> bind(
    from: TypeToken<From>,
    target: TypeToken<Target>,
    annotation: Annotation,
  )

  fun <From, Target : From> bind(
    from: TypeToken<From>,
    target: TypeToken<Target>,
    annotation: Class<out Annotation>,
  )

  fun <From, Target : From> bind(
    from: TypeToken<From>,
    target: TypeToken<Target>,
  )

  /**
   * Binds the mongodb [DataStoreContext]
   *
   * Using this method is the same as invoking:
   *
   * bind(new TypeLiteral<DataStoreContext<ObjectId, Datastore>>() {
   * }).to(MongoContext.class);
   */
  fun withMongoDB()

  /**
   * Binds the xodus [DataStoreContext]
   *
   * Using this method is the same as invoking:
   *
   * binder.bind(new TypeLiteral<DataStoreContext<EntityId, PersistentEntityStore>>() {
   * }).to(XodusContext.class);
   *
   */
  fun withXodus()

  companion object {
    fun <T> getTypeLiteral(typeToken: TypeToken<T>): TypeLiteral<T> {
      return TypeLiteral.get(typeToken.type) as TypeLiteral<T>
    }

    fun <T> getKey(typeToken: TypeToken<T>): Key<T>? {
      return Key.get(getTypeLiteral(typeToken))
    }

    fun <T> asInternalProvider(clazz: Class<T>): Provider<T> {
      return Provider { Anvil.getEnvironment().injector.getInstance(clazz) }
    }

    fun <T> asInternalProvider(typeLiteral: TypeLiteral<T>): Provider<T> {
      return Anvil.getEnvironment().injector.getProvider(Key.get(typeLiteral))
    }

    fun <T> asInternalProvider(typeToken: TypeToken<T>): Provider<T> {
      return Anvil.getEnvironment().injector.getProvider(getKey(typeToken))
    }
  }
}
