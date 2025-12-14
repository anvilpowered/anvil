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

import cats.syntax.traverse.*
import io.leangen.geantyref.TypeToken

class ListKey[E](
    override val typeTok: TypeToken[List[E]],
    override val name: String,
    override val fallback: List[E],
    override val description: Option[String],
    val elementType: TypeToken[E],
    val elementSerializer: KSerializer[E],
) extends Key[List[E]] {
//  private val namespace: KeyNamespace = this@KeyNamespace
  private val serializer = ListSerializer(elementSerializer)

//  init {
//    namespace.add(this)
//  }

  def serializeElement(
      element: E,
  ): String = ???
//    json.encodeToString(elementSerializer, element)

  def deserializeElement(
      element: String,
  ): E = ???
//    json.decodeFromString(elementSerializer, element.prepareForDecode(elementType))

  override def serialize(
      value: List[E],
  ): String = ???
  // Json.encodeToString(serializer, value)

  override def deserialize(
      value: String,
  ): List[E] = ???
//    Json.decodeFromString(serializer, value)

//  override def compareTo(other: Key[List[E]]): Int = Key.comparator.compare(this, other)

//  override def equals(other: Any): Boolean = (other as Key[*]?)?.let { Key.equals(this, it) } ?: false

//  override def hashCode(): Int = Key.hashCode(this)

  override def toString(): String = "ListKey[$elementType](name='$name')"
}

object ListKey {
  @KeyBuilderDsl
  trait BuilderFacet[E] extends Key.BuilderFacet[List[E], ListKey[E]] {

    /** Sets the element serializer of the generated [Key].
      *
      * This is entirely optional, as the default serializer will be used if this is not set. The default serializer requires the element type to be
      * trivially serializable or annotated with `@Serializable` from the kotlinx-serialization framework.
      *
      * @param serializer
      *   The element serializer to set or `null` to use the default
      * @return
      *   `this`
      */
    @KeyBuilderDsl
    def elementSerializer(serializer: KSerializer[E]): this.type
  }

  @KeyBuilderDsl
  trait AnonymousBuilderFacet[E] extends BuilderFacet[E], Key.BuilderFacet[List[E], ListKey[E]]

  @KeyBuilderDsl
  trait NamedBuilderFacet[E] extends BuilderFacet[E], Key.NamedBuilderFacet[List[E], ListKey[E]]

  @KeyBuilderDsl
  trait Builder[E] extends BuilderFacet[E], Key.Builder[List[E], ListKey[E]]

  @KeyBuilderDsl
  trait FacetedBuilder[E] extends BuilderFacet[E], Key.FacetedBuilder[List[E], ListKey[E], AnonymousBuilderFacet[E], NamedBuilderFacet[E]]
}
