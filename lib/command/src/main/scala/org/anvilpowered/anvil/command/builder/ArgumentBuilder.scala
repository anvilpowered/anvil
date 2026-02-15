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
package org.anvilpowered.anvil.command.builder

import cats.data.EitherT
import cats.effect.Async
import org.anvilpowered.anvil.command.Command
import org.anvilpowered.anvil.command.argument.ArgumentType
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.exception.CommandError
import org.anvilpowered.anvil.command.tree.CommandNode

import scala.collection.mutable
import net.kyori.adventure.text.Component

abstract class ArgumentBuilder[S] {

//    private val _children = mutableMapOf<String, CommandNode<S>>()
  private val _children = new mutable.HashMap[String, CommandNode[S]]
  val children: Map[String, CommandNode[S]] = _children.toMap

  var command: Option[Command[S]] = None
//        private set
  var requirement: S => Boolean = { _ => true }
//        private set
  var forward: Option[CommandNode[S]] = None
//        private set
  var forks = false
//        private set

//    protected abstract val self: B

  def thenArg(argument: CommandNode[S]): this.type = {
//        check(redirect == null) { "Cannot add children to a redirected node" }
//        _children[argument.name] = argument
    this
  }

  def thenArg(argument: ArgumentBuilder[S]): this.type = thenArg(argument.build())

  def executesOption(command: Option[Command[S]]): this.type = {
    this.command = command
    this
  }

  def executesCmd(command: Command[S]): this.type = executesOption(Some(command))

  def executes(command: [F[_]: Async] => CommandContext[S] => EitherT[F, CommandError, Component]): this.type =
    executesCmd(new Command[S] {
      override def execute[F[_]: Async](context: CommandContext[S]): EitherT[F, CommandError, Component] =
        command[F](context)
    })

  def requires(requirement: S => Boolean): this.type = {
    this.requirement = requirement
    this
  }

  def forward(target: Option[CommandNode[S]], forks: Boolean): this.type = {
//    check(children.isEmpty()) { "Cannot forward a node with children" }
    forward = target
    this.forks = forks
    this
  }

  def redirect(target: CommandNode[S]): this.type = forward(Some(target), false)
  def fork(target: CommandNode[S]): this.type = forward(Some(target), true)

  def build(): CommandNode[S]
}

object ArgumentBuilder {
  def literal[S](literal: String): LiteralArgumentBuilder[S] = LiteralArgumentBuilder[S](literal)
  def required[S, T](name: String, argType: ArgumentType[S, T]): RequiredArgumentBuilder[S, T] = RequiredArgumentBuilder[S, T](name, argType)
}
