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

package org.anvilpowered.anvil.core.command

import cats.Monad
import cats.data.{OptionT, ReaderT}
import cats.effect.Concurrent
import cats.implicits.toFlatMapOps
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.core.kbrig.argument.StringArgumentType
import org.anvilpowered.anvil.core.kbrig.builder.{ArgumentBuilder, RequiredArgumentBuilder}
import org.anvilpowered.anvil.core.kbrig.context.*
import org.anvilpowered.anvil.core.kbrig.suggestion.Suggestions.SuggestT
import org.anvilpowered.anvil.core.kbrig.suggestion.{SuggestionProvider, Suggestions}
import org.anvilpowered.anvil.core.user.{Player, PlayerService}

object PlayerArgument {
  def requiredPlayedArgument(
      playerService: PlayerService,
      argumentName: String = "player",
//          command:
  ): RequiredArgumentBuilder[Player, String] = ???
  //      builder.required[CommandSource, String]("player", )

  extension [S](builder: RequiredArgumentBuilder[S, String]) {
    def suggestPlayerArgument(using playerService: PlayerService): RequiredArgumentBuilder[S, String] =
      builder.suggests(new SuggestionProvider[S] {
        override def suggest[F[_]: Concurrent](context: CommandContext[S]): SuggestT[F] =
          Suggestions.of(ReaderT(playerService.getAll), _.username)
      })
  }

  def extract[F[_]: Monad](
      context: CommandContext[CommandSource],
      argumentName: String = "player",
  )(using playerService: PlayerService): OptionT[F, Player] = {
    for {
      playerName <- context.extract[F, String](argumentName)
      player <- playerService.get[F](playerName)
    } yield player
  }
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
//@CommandContextScopeDsl
//suspend def CommandExecutionScope[CommandSource].extractPlayerArgument(
//  playerService: PlayerService,
//  argumentName: String = "player",
//): Player {
//  val playerName = context.get[String](argumentName)
//  val player = playerService[playerName]
//  if (player == null) {
//    context.source.sendMessage(
//      Component
//        .text()
//        .append(Component.text("Player with name ", NamedTextColor.RED))
//        .append(Component.text(playerName, NamedTextColor.GOLD))
//        .append(Component.text(" not found!", NamedTextColor.RED))
//        .build(),
//    )
//    yieldError()
//  }
//  return player
//}
//
//@CommandContextScopeDsl
//suspend def CommandExecutionScope[CommandSource].extractPlayerSource(): Player {
//  val player = extractPlayerSourceOrNull()
//  if (player == null) {
//    context.source.sendMessage(Component.text("You must be a player to use this command!", NamedTextColor.RED))
//    yieldError()
//  }
//  return player
//}
//
//@CommandContextScopeDsl
//def CommandContext.Scope[CommandSource].extractPlayerSourceOrNull(): Player? = context.source.player
//
//@CommandContextScopeDsl
//suspend def CommandContext.Scope[CommandSource].extractPlayerSourceOrAbort(): Player = extractPlayerSourceOrNull() ?: abort()
