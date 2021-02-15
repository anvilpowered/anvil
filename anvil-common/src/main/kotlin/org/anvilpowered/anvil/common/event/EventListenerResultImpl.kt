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

package org.anvilpowered.anvil.common.event

import org.anvilpowered.anvil.api.event.EventListenerResult

class EventListenerResultImpl(private val success: Boolean) : EventListenerResult {
  override fun isSuccess(): Boolean = success
  class Builder : EventListenerResult.Builder {
    private var success: Boolean? = null
    override fun success(success: Boolean): Builder {
      this.success = success
      return this
    }

    override fun build(): EventListenerResultImpl {
      return EventListenerResultImpl(success!!)
    }
  }
}
