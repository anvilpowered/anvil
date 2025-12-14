/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.exception

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
