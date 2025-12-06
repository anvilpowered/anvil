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
import net.kyori.adventure.text.Component
import java.time.ZoneId

@Suppress("PropertyName")
open class TypeTokens {
  val BOOLEAN: TypeToken<Boolean> = TypeToken.get(Boolean::class.javaObjectType)
  val INTEGER: TypeToken<Int> = TypeToken.get(Int::class.javaObjectType)
  val STRING: TypeToken<String> = TypeToken.get(String::class.java)
  val COMPONENT: TypeToken<Component> = TypeToken.get(Component::class.java)
  val ZONE_ID: TypeToken<ZoneId> = TypeToken.get(ZoneId::class.java)

  companion object : TypeTokens()
}
