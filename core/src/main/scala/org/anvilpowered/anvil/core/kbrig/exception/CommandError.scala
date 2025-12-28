package org.anvilpowered.anvil.core.kbrig.exception

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

import java.lang.reflect.Type

trait CommandError {
  def text: Component
}

trait ArgumentError extends CommandError {
  def argument: String
}

case class NoArgError(override val argument: String) extends ArgumentError {
  override val text: Component = Component
    .text()
    .append(Component.text("No such argument ").color(NamedTextColor.RED))
    .append(Component.text(argument).color(NamedTextColor.GOLD))
    .append(Component.text(" exists on this command").color(NamedTextColor.RED))
    .build()
}

case class NotFoundError(argType: String, override val argument: String) extends ArgumentError {
  override def text: Component = Component
    .text()
    .append(Component.text(s"Could not find $argType ").color(NamedTextColor.RED))
    .append(Component.text(argument).color(NamedTextColor.GOLD))
    .append(Component.text("!").color(NamedTextColor.RED))
    .build()
}

case class ArgTypeCastError(argType: String, expected: Type, actual: Type, override val argument: String) extends ArgumentError {
  override def text: Component = Component
    .text()
    .append(Component.text(s"Expected $argType argument $argument of type ").color(NamedTextColor.RED))
    .append(Component.text(expected.getTypeName).color(NamedTextColor.GOLD))
    .append(Component.text(" but was "))
    .append(Component.text(actual.getTypeName).color(NamedTextColor.GOLD))
    .append(Component.text("!").color(NamedTextColor.RED))
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
