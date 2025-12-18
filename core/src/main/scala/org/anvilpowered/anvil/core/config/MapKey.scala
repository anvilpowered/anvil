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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.Json

class MapKey[K, V](
  override val typeTok: TypeToken[Map[K, V]],
  override val name: String,
  override val fallback: Map[K, V],
  override val description: Option[String],
  val keyType: TypeToken[K],
  val keySerializer: KSerializer[K],
  val valueType: TypeToken[V],
  val valueSerializer: KSerializer[V],
) extends Key[Map[K, V]] {
  private val serializer = MapSerializer(keySerializer, valueSerializer)

  def serializeKey(
    key: K,
    json: Json = Json,
  ): String = json.encodeToString(keySerializer, key)

  def deserializeKey(
    key: String,
    json: Json = Json,
  ): K = json.decodeFromString(keySerializer, key.prepareForDecode(keyType))

  def serializeValue(
    value: V,
    json: Json = Json,
  ): String = json.encodeToString(valueSerializer, value)

  def deserializeValue(
    value: String,
    json: Json = Json,
  ): V = json.decodeFromString(valueSerializer, value.prepareForDecode(valueType))

  override def serialize(
    value: Map[K, V],
    json: Json,
  ): String = json.encodeToString(serializer, value)

  override def deserialize(
    value: String,
    json: Json,
  ): Map[K, V] = json.decodeFromString(serializer, value)

  override def toString: String = s"MapKey[$keyType, $valueType](name='$name')"

}

object MapKey {

  @KeyBuilderDsl
  trait BuilderFacet[K, V] extends Key.BuilderFacet[Map[K, V], MapKey[K, V]] {
    /**
     * Sets the key serializer of the generated [Key].
     *
     * This is entirely optional, as the default serializer will be used if this is not set.
     * The default serializer requires the element type to be trivially serializable or annotated with `@Serializable`
     * from the kotlinx-serialization framework.
     *
     * @param serializer The key serializer to set or `null` to use the default
     * @return `this`
     */
    @KeyBuilderDsl
    def keySerializer(serializer: KSerializer[K]?): B

    /**
     * Sets the value serializer of the generated [Key].
     *
     * This is entirely optional, as the default serializer will be used if this is not set.
     * The default serializer requires the element type to be trivially serializable or annotated with `@Serializable`
     * from the kotlinx-serialization framework.
     *
     * @param serializer The value serializer to set or `null` to use the default
     * @return `this`
     */
    @KeyBuilderDsl
    def valueSerializer(serializer: KSerializer[V]?): B
  }

  @KeyBuilderDsl
  trait AnonymousBuilderFacet[K, V] extends
    BuilderFacet[K, V],
    Key.BuilderFacet[Map[K, V], MapKey[K, V]]

  @KeyBuilderDsl
  trait NamedBuilderFacet[K, V] extends
    BuilderFacet[K, V],
    Key.NamedBuilderFacet[Map[K, V], MapKey[K, V]]

  @KeyBuilderDsl
  trait Builder[K, V] extends
    BuilderFacet[K, V],
    Key.Builder[Map[K, V], MapKey[K, V]]

  @KeyBuilderDsl
  trait FacetedBuilder[K, V] extends
    BuilderFacet[K, V],
    Key.FacetedBuilder[Map[K, V], MapKey[K, V], FacetedBuilder[K, V], NamedBuilderFacet[K, V]]
}