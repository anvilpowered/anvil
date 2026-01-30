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

import scala.io.circe.parser.decode

/** A [[Registry]] implementation that checks environment variables.
  * TODO: Pulling environment variables is not pure. They should all be parsed at launch and all errors printed.
  */
class EnvironmentRegistry(
    private val prefix: String,
    private val delegate: Option[Registry] = None,
) extends Registry {
  private def envName(key: Key[?]): String = prefix + "_" + key.name

  override def getDefault[T](key: Key[T]): T = delegate.map(_.getDefault(key)).getOrElse(key.fallback)
  override def get[T](key: Key[T]): Option[T] = {
    Option(System.getenv(envName(key)))
      .flatMap(decode[T](_)(using key.codec).toOption) // TODO: Error is ignored
      .orElse(delegate.flatMap(_.get(key)))
  }
}
