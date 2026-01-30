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

import java.time.ZoneId
import scala.io.leangen.geantyref.TypeToken

object TypeTokens {
  val BOOLEAN: TypeToken[Boolean] = TypeToken.get(classOf[Boolean])
  val INTEGER: TypeToken[Int] = TypeToken.get(classOf[Int])
  val STRING: TypeToken[String] = TypeToken.get(classOf[String])
  val COMPONENT: TypeToken[Component] = TypeToken.get(classOf[Component])
  val ZONE_ID: TypeToken[ZoneId] = TypeToken.get(classOf[ZoneId])
}
