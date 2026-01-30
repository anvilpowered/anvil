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
package org.anvilpowered.anvil.command.exception

class CommandSyntaxException(message: String) extends Exception(message) {

  def this(message: String, input: String, cursor: Int) =
    this(message + CommandSyntaxException.getContext(input, cursor))

}
object CommandSyntaxException {
  val CONTEXT_AMOUNT = 10

  def getContext(input: String, cursor: Int): String = {
    require(cursor >= 0)
    val builder = new StringBuilder(" at position $cursor: ")
    val adjustedCursor = math.min(input.length, cursor)
    if (adjustedCursor > CONTEXT_AMOUNT) {
      builder.append("...")
    }
    builder.append(
      input,
      math.max(0, adjustedCursor - CONTEXT_AMOUNT),
      adjustedCursor
    )
    builder.append("<--[HERE]")
    builder.toString()
  }
}
