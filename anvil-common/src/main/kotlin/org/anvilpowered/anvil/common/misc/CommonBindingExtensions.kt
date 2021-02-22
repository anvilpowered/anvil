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

package org.anvilpowered.anvil.common.misc

import com.google.common.reflect.TypeToken
import com.google.inject.Binder
import com.google.inject.TypeLiteral
import dev.morphia.Datastore
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import org.anvilpowered.anvil.api.datastore.DBComponent
import org.anvilpowered.anvil.api.datastore.DataStoreContext
import org.anvilpowered.anvil.api.datastore.MongoContext
import org.anvilpowered.anvil.api.datastore.XodusContext
import org.anvilpowered.anvil.api.misc.BindingExtensions
import org.bson.types.ObjectId

@Suppress("unchecked", "UnstableApiUsage")
class CommonBindingExtensions(val binder: Binder) : BindingExtensions {

  override fun <From1 : DBComponent<*, *>, From2 : DBComponent<*, *>, From3 : From1, Target : From1> bind(
    from1: TypeToken<From1>,
    from2: TypeToken<From2>,
    from3: TypeToken<From3>,
    target: TypeToken<Target>,
    componentAnnotation: Annotation,
  ) {
    binder.bind(TypeLiteral.get(from2.type) as TypeLiteral<From1>)
      .annotatedWith(componentAnnotation)
      .to(BindingExtensions.getTypeLiteral(target))
    binder.bind(TypeLiteral.get(from3.type) as TypeLiteral<From1>)
      .to(BindingExtensions.getTypeLiteral(target))
  }

  override fun <From1 : DBComponent<*, *>, From2 : From1, Target : From1> bind(
    from1: TypeToken<From1>,
    from2: TypeToken<From2>,
    target: TypeToken<Target>,
    componentAnnotation: Annotation,
  ) {
    bind(from1, from1, from2, target, componentAnnotation)
  }

  override fun <From, Target : From> bind(
    from: TypeToken<From>,
    target: TypeToken<Target>,
    annotation: Annotation,
  ) {
    binder.bind(BindingExtensions.getTypeLiteral(from))
      .annotatedWith(annotation)
      .to(BindingExtensions.getTypeLiteral(target))
  }

  override fun <From, Target : From> bind(
    from: TypeToken<From>,
    target: TypeToken<Target>,
    annotation: Class<out Annotation>,
  ) {
    binder.bind(BindingExtensions.getTypeLiteral(from))
      .annotatedWith(annotation)
      .to(BindingExtensions.getTypeLiteral(target))
  }

  override fun <From, Target : From> bind(
    from: TypeToken<From>,
    target: TypeToken<Target>,
  ) {
    binder.bind(BindingExtensions.getTypeLiteral(from))
      .to(BindingExtensions.getTypeLiteral(target))
  }

  override fun withMongoDB() {
    binder.bind(object : TypeLiteral<DataStoreContext<ObjectId, Datastore>>() {})
      .to(MongoContext::class.java)
  }

  override fun withXodus() {
    binder.bind(object : TypeLiteral<DataStoreContext<EntityId, PersistentEntityStore>>() {})
      .to(XodusContext::class.java)
  }
}
