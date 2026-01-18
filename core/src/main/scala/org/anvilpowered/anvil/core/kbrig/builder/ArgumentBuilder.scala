/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.builder

import cats.data.EitherT
import cats.effect.Async
import org.anvilpowered.anvil.core.kbrig.Command
import org.anvilpowered.anvil.core.kbrig.argument.ArgumentType
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.CommandError
import org.anvilpowered.anvil.core.kbrig.tree.CommandNode

import scala.collection.mutable

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

  def executes(command: [F[_]: Async] => CommandContext[S] => EitherT[F, CommandError, Int]): this.type =
    executesCmd(new Command[S] {
      override def execute[F[_]: Async](context: CommandContext[S]): EitherT[F, CommandError, Int] =
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
