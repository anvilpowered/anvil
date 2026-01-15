package org.anvilpowered.anvil.core.kbrig.exception

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.spongepowered.configurate.ConfigurateException

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

case class AlreadyExistsReplaceError(argType: String, override val argument: String, newPath: Option[String]) extends ArgumentError {
  override def text: Component = Component
    .text()
    .append(Component.text(s"${argType.capitalize} ").color(NamedTextColor.RED))
    .append(Component.text(argument).color(NamedTextColor.GOLD))
    .append(Component.text(" already exists!").color(NamedTextColor.RED))
    .append(Component.newline())
    .append(Component.text("Use --force to ", NamedTextColor.RED))
    .append(
      newPath match {
        case Some(value) =>
          Component
            .text()
            .append(Component.text("replace with ", NamedTextColor.RED))
            .append(Component.text(value, NamedTextColor.GOLD))
            .append(Component.text(".", NamedTextColor.RED))
        case None =>
          Component.text("overwrite.", NamedTextColor.RED)
      },
    )
    .build()
}

case class CouldNotDeleteError(argType: String, override val argument: String) extends ArgumentError {
  override def text: Component = Component
    .text()
    .append(Component.text(s"Could not delete $argType ").color(NamedTextColor.RED))
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

case class ConfigurateCommandError(ex: ConfigurateException) extends CommandError {
  override def text: Component = Component
    .text()
    .append(Component.text(s"A configurate exception occurred: ${ex.getClass}"))
    .append(Component.newline())
    .append(Component.text(ex.getMessage))
    .color(NamedTextColor.RED)
    .build()
}
