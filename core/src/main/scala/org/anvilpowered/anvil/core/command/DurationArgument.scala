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

import cats.data.{EitherT, OptionT, Validated}
import cats.syntax.all.*
import cats.{Functor, Monad, MonadError}
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.{NamedTextColor, TextDecoration}
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.{CommandError, ParseError}

import java.time.Duration
import java.util.regex.{Matcher, Pattern}

object DurationArgument {
  private val SecondsInMinute = 60L
  private val SecondsInHour = 3600L
  private val SecondsInDay = 86400L
  private val SecondsInWeek = 604800L
  private val SecondsInMonth = 2592000L
  private val SecondsInYear = 31536000L

  private val timePattern: Pattern = Pattern.compile(
    """(?x)
      (?:(?<years>-?\d+)y)?\s*
      (?:(?<months>-?\d+)M)?\s*
      (?:(?<weeks>-?\d+)w)?\s*
      (?:(?<days>-?\d+)d)?\s*
      (?:(?<hours>-?\d+)h)?\s*
      (?:(?<minutes>-?\d+)m)?\s*
      (?:(?<seconds>-?\d+)s)?
    """.trim,
  )

  private val errorMessage = Component
    .text()
    .append(Component.text("Invalid duration format", NamedTextColor.RED, TextDecoration.BOLD))
    .append(Component.newline())
    .append(Component.text("Expected format (example): ", NamedTextColor.GRAY))
    .append(Component.text("1y 2M 3w 4d 5h 6m 7s", NamedTextColor.GOLD))
    .append(Component.newline())
    .append(Component.text("Important:", NamedTextColor.GRAY, TextDecoration.BOLD))
    .append(Component.newline())
    .append(Component.text(" - Order matters", NamedTextColor.GRAY))
    .append(Component.newline())
    .append(Component.text(" - Provide at least one value", NamedTextColor.GRAY))
    .append(Component.newline())
    .append(Component.text(" - Negatives values are accepted (e.g. 5h -30m == 4h 30m)", NamedTextColor.GRAY))
    .build()

  private val MissingError: Component =
    Component.text("Duration argument is required", NamedTextColor.RED)

  private def parse(input: String): Either[ParseError, Duration] = {
    val matcher = timePattern.matcher(input)

    Either.cond(
      !matcher.matches() || input.isBlank,
      calculateDuration(matcher),
      ParseError(input, "Duration argument is required"),
    )
  }

  private def calculateDuration(matcher: Matcher): Duration = {
    def get(name: String): Long =
      Option(matcher.group(name))
        .flatMap(_.toLongOption)
        .getOrElse(0L)

    val totalSeconds = (get("years") * SecondsInYear) +
      (get("months") * SecondsInMonth) +
      (get("weeks") * SecondsInWeek) +
      (get("days") * SecondsInDay) +
      (get("hours") * SecondsInHour) +
      (get("minutes") * SecondsInMinute) +
      get("seconds")

    Duration.ofSeconds(totalSeconds)
  }

  def extract[F[_]: Monad](
      context: CommandContext[CommandSource],
      argumentName: String = "duration",
  ): EitherT[F, CommandError, Duration] =
  context.extract[F, String](argumentName)
    .subflatMap(parse)
}
