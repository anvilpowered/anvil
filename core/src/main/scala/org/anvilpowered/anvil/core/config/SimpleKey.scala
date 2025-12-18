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

package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeToken

class SimpleKey[T](
    override val typeTok: TypeToken[T],
    override val name: String,
    override val fallback: T,
    override val description: Option[String],
    val serializer: KSerializer[T],
) extends Key[T] {

  override def serialize(
      value: T,
      json: Json,
  ): String = json.encodeToString(serializer, value)

  override def deserialize(
      value: String,
      json: Json,
  ): T = json.decodeFromString(serializer, value.prepareForDecode(typeTok))

  override def toString: String = s"SimpleKey(type=$typeTok, name='$name')"

}

object SimpleKey {
  @KeyBuilderDsl
  trait BuilderFacet[T] extends Key.BuilderFacet[T, SimpleKey[T]] {

    /** Sets the serializer of the generated [Key].
      *
      * This is entirely optional, as the default serializer will be used if this is not set. The default serializer requires the element type to be
      * trivially serializable or annotated with `@Serializable` from the kotlinx-serialization framework.
      *
      * @param serializer
      *   The serializer to set or `null` to use the default
      * @return
      *   `this`
      */
    @KeyBuilderDsl
    def serializer(serializer: Option[KSerializer[T]]): B
  }

  @KeyBuilderDsl
  trait AnonymousBuilderFacet[T] extends BuilderFacet[T], Key.BuilderFacet[T, SimpleKey[T]]

  @KeyBuilderDsl
  trait NamedBuilderFacet[T] extends BuilderFacet[T], Key.NamedBuilderFacet[T, SimpleKey[T]]

  @KeyBuilderDsl
  trait Builder[T] extends BuilderFacet[T], Key.Builder[T, SimpleKey[T]]

  @KeyBuilderDsl
  trait FacetedBuilder[T] extends BuilderFacet[T], Key.FacetedBuilder[T, SimpleKey[T], AnonymousBuilderFacet[T], NamedBuilderFacet[T]]
}
