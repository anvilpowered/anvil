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
package org.anvilpowered.anvil.api.registry

import java.lang.AssertionError
import com.google.common.reflect.TypeToken
import java.time.ZoneId

class TypeTokens private constructor() {
  init {
    throw AssertionError("**boss music** No instance for you!")
  }

  companion object {
    val BOOLEAN = TypeToken.of(Boolean::class.java)
    val INTEGER = TypeToken.of(Int::class.java)
    val STRING = TypeToken.of(
      String::class.java)
    val ZONE_ID = TypeToken.of(ZoneId::class.java)
  }
}
