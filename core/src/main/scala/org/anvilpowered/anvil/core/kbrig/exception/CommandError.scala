package org.anvilpowered.anvil.core.kbrig.exception

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

trait CommandError {
  def text: Component
}

trait ArgumentError {
  def argument: String
  def text: Component
}

case class NoArg(override val argument: String) extends ArgumentError {
  override val text: Component = Component
    .text()
    .append(Component.text("No such argument ").color(NamedTextColor.RED))
    .append(Component.text(argument).color(NamedTextColor.GOLD))
    .append(Component.text(" exists on this command").color(NamedTextColor.RED))
    .build()
}

case class ParseError(input: String, message: String) extends CommandError {
  val text: Component = Component
    .text()
    .append(Component.text("Could not parse").color(NamedTextColor.RED))
    .append(Component.text(input).color(NamedTextColor.GOLD))
    .append(Component.text(": " + message).color(NamedTextColor.RED))
    .build()
}
