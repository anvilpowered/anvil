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

package org.anvilpowered.anvil.common.anvilnet

import org.anvilpowered.anvil.api.anvilnet.AnvilNetNode
import org.anvilpowered.anvil.api.anvilnet.NodeFilter
import org.anvilpowered.anvil.common.anvilnet.communicator.node.Node
import java.util.regex.Pattern

sealed class NodeFilterImpl : NodeFilter {

  val children: MutableList<NodeFilterImpl> = mutableListOf()

  object All : NodeFilterImpl() {
    override fun test(t: AnvilNetNode): Boolean = true
  }

  override fun test(t: AnvilNetNode): Boolean {
    TODO("Not yet implemented")
  }

  class Builder(predicate: (Node) -> Boolean) : NodeFilter.Builder {

    class Matcher(predicate: (Node) -> Boolean) : NodeFilter.Builder.Matcher {
      override fun include(): NodeFilter.Builder {
        TODO("Not yet implemented")
      }

      override fun exclude(): NodeFilter.Builder {
        TODO("Not yet implemented")
      }
    }

    override fun all(): NodeFilter.Builder.Matcher {
      TODO("Not yet implemented")
    }

    override fun platformName(platform: String): NodeFilter.Builder.Matcher {
      return Matcher { it.platform.name == platform }
    }

    override fun platformIsProxy(isProxy: Boolean): NodeFilter.Builder.Matcher {
      return Matcher { it.platform.isProxy }
    }

    override fun nodeName(name: String): NodeFilter.Builder.Matcher {
      return Matcher { it.ref.name == name }
    }

    override fun nodeName(name: Pattern): NodeFilter.Builder.Matcher {
      return Matcher { name.matcher(it.ref.name).matches() }
    }

    override fun build(): NodeFilter {
      TODO("Not yet implemented")
    }
  }
}
