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

package org.anvilpowered.anvil.platform.command

import cats.Monad
import cats.data.{EitherT, OptionT, ReaderT}
import cats.implicits.toFlatMapOps
import org.anvilpowered.anvil.command.suggestion.Suggestions.SuggestT
import cats.effect.kernel.Async
import org.anvilpowered.anvil.command.builder.RequiredArgumentBuilder
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.exception.CommandError
import org.anvilpowered.anvil.command.suggestion.Suggestions
import org.anvilpowered.anvil.platform.user.PlayerService
import org.anvilpowered.anvil.platform.user.Player
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.command.exception.NotFoundError

object PlayerArgument {
  extension [S](builder: RequiredArgumentBuilder[S, String]) {
    def suggestPlayerArgument(using playerService: PlayerService): builder.type =
      builder.suggests([F[_]: Async] => _ => Suggestions.ofStream(ReaderT(playerService.getAll), _.username.value))
  }

  /** Extract a player from an argument.
    */
  def extract[F[_]: Monad](
      context: CommandContext[CommandSource],
      argumentName: String = "player",
  )(using playerService: PlayerService): EitherT[F, CommandError, Player] =
    for {
      playerName <- context.extract[F, String](argumentName)
      player <- playerService
        .get[F](playerName)
        .toRight(NotFoundError("player", playerName))
    } yield player

  def extractSource[F[_]: Monad](
      context: CommandContext[CommandSource],
  ): EitherT[F, CommandError, Player] =
    EitherT.fromEither(context.source.player.toRight(MustBePlayerError))
}

case object MustBePlayerError extends CommandError {
  override def text: Component = Component
    .text("You must be a player to use this command!")
    .color(NamedTextColor.RED)
}
//def ArgumentBuilder.Companion.requirePlayerArgument(
//  playerService: PlayerService,
//  argumentName: String = "player",
//  command: suspend (context: CommandContext[CommandSource], player: Player) -] Int,
//): RequiredArgumentBuilder[CommandSource, String] =
//  required[CommandSource, String]("player", StringArgumentType.SingleWord)
//    .suggestPlayerArgument(playerService)
//    .executesScoped { yield(command(context, extractPlayerArgument(playerService, argumentName))) }
//
//def ArgumentBuilder.Companion.requirePlayerArgumentScoped(
//  playerService: PlayerService,
//  argumentName: String = "player",
//  command: suspend CommandExecutionScope[CommandSource].(player: Player) -] Unit,
//): RequiredArgumentBuilder[CommandSource, String] =
//  required[CommandSource, String]("player", StringArgumentType.SingleWord)
//    .suggestPlayerArgument(playerService)
//    .executesScoped { command(extractPlayerArgument(playerService, argumentName)) }
//
//def [S] RequiredArgumentBuilder[S, String].suggestPlayerArgument(playerService: PlayerService): RequiredArgumentBuilder[S, String] =
//  suggestsScoped { playerService.getAll().suggestAllFiltered { it.username } }
//
