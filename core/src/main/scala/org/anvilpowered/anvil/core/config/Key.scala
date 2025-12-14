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

trait Key[T] {
  val typeTok: TypeToken[T]

  val name: String

  val fallback: T

  val description: Option[String]

  /** Serializes the given value in a simple [String] representation.
    */
  def serialize(value: T): String

  /** TODO: Option -> Either with error message
    *
    * <p> Deserializes the given value from a simple [String] representation.
    */
  def deserialize(value: String): Option[T]
}

object Key {
  given ordering: Ordering[Key[?]] = Ordering.by(_.name)

  @KeyBuilderDsl
  trait BuilderFacet[T, K <: Key[T]] {

    /** Sets the fallback value of the generated [Key].
      *
      * This value is used when a [Registry] does not have a value or default value for this key.
      *
      * @param fallback
      *   The fallback value to set
      * @return
      *   `this`
      */
    @KeyBuilderDsl
    def fallback(fallback: Option[T]): this.type

    /** Sets the description of the generated [Key].
      *
      * This is used for documentation; for example, in a configuration file.
      *
      * @param description
      *   The description to set or `null` to remove it
      * @return
      *   `this`
      */
    @KeyBuilderDsl
    def description(description: Option[String]): this.type
  }

  @KeyBuilderDsl
  trait NamedBuilderFacet[T, K <: Key[T]] extends BuilderFacet[T, K] {

    /** Sets the name of the generated [Key].
      *
      * The name is used to identify the key in a [Registry]. It is also used as an underscore-separated node path in configuration files.
      *
      * Example: `JOIN_LISTENER_ENABLED` will become (in HOCON):
      *
      * ```
      * join {
      *   listener {
      *     enabled = ...
      *   }
      * }
      * ```
      *
      * @param name
      *   The name to set
      * @return
      *   `this`
      */
    @KeyBuilderDsl
    def name(name: String): this.type
  }

  trait Builder[T, K <: Key[T]] extends NamedBuilderFacet[T, K] {

    /** Generates a [Key] based on this builder.
      *
      * @return
      *   The generated [Key]
      */
    @KeyBuilderDsl
    def build()(using keyNamespace: KeyNamespace): this.type
  }

  @KeyBuilderDsl
  trait FacetedBuilder[
      T,
      K <: Key[T],
      AF <: BuilderFacet[T, K],
      NF <: NamedBuilderFacet[T, K],
  ] extends Builder[T, K] {

    /** @return
      *   This builder as an (anonymous) [BuilderFacet]
      */
    def asAnonymousFacet(): AF

    /** @return
      *   This builder as a [NamedBuilderFacet]
      */
    def asNamedFacet(): NF
  }
}
