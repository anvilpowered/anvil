/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2026 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.config

import org.jetbrains.annotations.ApiStatus

import scala.collection.mutable
import io.leangen.geantyref.TypeToken
import scala.reflect.ClassTag

trait KeyNamespace {
  val name: String

  val keys: Set[Key[?]]

  def get[T: TypeToken](keyName: String): Option[Key[T]]

  def add[T](key: Key[T]): Unit
}

object KeyNamespace {
  def create(name: String): KeyNamespace = KeyNamespaceImpl(name)

  extension (namespace: KeyNamespace) {
    def apply[T](keyName: String)(using tag: ClassTag[T]): Option[Key[T]] =
      namespace.get(keyName)(using TypeToken.get[T](tag.runtimeClass.asInstanceOf[Class[T]]))
  }
}

private class KeyNamespaceImpl(override val name: String) extends KeyNamespace {
  private val keyMap: mutable.Map[String, Key[?]] = mutable.Map()

  private val _keys: mutable.Set[Key[?]] = mutable.Set()
  override val keys: Set[Key[?]] = _keys.toSet

  override def get[T](keyName: String)(using typeTok: TypeToken[T]): Option[Key[T]] = {
    for {
      key <- keyMap.get(keyName)
    } yield {
      if (key.typeToken == typeTok) {
        key.asInstanceOf[Key[T]]
      } else {
        throw new ClassCastException(s"Key $name has type ${key.typeToken} which does not match provided type $typeTok")
      }
    }
  }

  override def add[T](key: Key[T]): Unit = {
    require(keyMap.put(key.name, key).isEmpty, s"Key with name ${key.name} already exists")
    assert(_keys.add(key), "Unable to add key")
  }
}
