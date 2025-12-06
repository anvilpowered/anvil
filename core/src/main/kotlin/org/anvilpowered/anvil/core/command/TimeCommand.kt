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

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.anvilpowered.kbrig.context.CommandContextScopeDsl
import org.anvilpowered.kbrig.context.CommandExecutionScope
import org.anvilpowered.kbrig.context.get
import org.anvilpowered.kbrig.context.yieldError
import java.time.Duration
import java.util.regex.Pattern

@CommandContextScopeDsl
suspend fun CommandExecutionScope<CommandSource>.extractDurationArgument(
  argumentName: String = "duration",
): Duration {
  val durationString = context.get<String>(argumentName)
  val matcher = timePattern.matcher(durationString)
  if (!matcher.matches() || durationString.isBlank()) {
    context.source.sendMessage(
      Component.text()
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
        .append(Component.text(" - Negatives values are accepted (e.g. 5h -30m == 4h 30m)", NamedTextColor.GRAY)),
    )
    yieldError()
  }
  val years = matcher.group("years")?.toLongOrNull() ?: 0
  val months = matcher.group("months")?.toLongOrNull() ?: 0
  val weeks = matcher.group("weeks")?.toLongOrNull() ?: 0
  val days = matcher.group("days")?.toLongOrNull() ?: 0
  val hours = matcher.group("hours")?.toLongOrNull() ?: 0
  val minutes = matcher.group("minutes")?.toLongOrNull() ?: 0
  val seconds = matcher.group("seconds")?.toLongOrNull() ?: 0
  return Duration.ofSeconds(
    years * SECONDS_IN_YEAR +
      months * SECONDS_IN_MONTH +
      weeks * SECONDS_IN_WEEK +
      days * SECONDS_IN_DAY +
      hours * SECONDS_IN_HOUR +
      minutes * SECONDS_IN_MINUTE +
      seconds,
  )
}

fun Duration.format(maxCharacters: Int = -1, maxUnits: Int = -1): String {
  if (maxCharacters == 0 || maxUnits == 0) {
    return ""
  }
  if (seconds == 0L) {
    return if (nano > 0) "$nano nanoseconds" else "0 seconds"
  }

  data class State(
    val result: String,
    val secondsLeft: Long,
    val maxCharacters: Int,
    val maxUnits: Int,
    val fallThrough: Boolean = false,
  )

  val initialState = State(
    result = "",
    secondsLeft = seconds,
    maxCharacters = maxCharacters.takeIf { it >= 0 } ?: Int.MAX_VALUE,
    maxUnits = maxUnits.takeIf { it >= 0 } ?: Int.MAX_VALUE,
  )

  return sequenceOf(
    "year" to SECONDS_IN_YEAR,
    "month" to SECONDS_IN_MONTH,
    "week" to SECONDS_IN_WEEK,
    "day" to SECONDS_IN_DAY,
    "hour" to SECONDS_IN_HOUR,
    "minute" to SECONDS_IN_MINUTE,
    "second" to 1L,
  ).fold(initialState) { state, (label, divisor) ->
    if (state.fallThrough) {
      return@fold state
    }

    val result = when (val num = state.secondsLeft / divisor) {
      0L -> return@fold state
      1L -> "${state.result}$num $label, "
      else -> "${state.result}$num ${label}s, "
    }

    state.copy(
      result = result,
      secondsLeft = state.secondsLeft % divisor,
      maxCharacters = state.maxCharacters - result.length,
      maxUnits = state.maxUnits - 1,
    ).takeIf { it.maxCharacters >= 0 && it.maxUnits >= 0 }
      ?: state.copy(fallThrough = true)
  }.result.substringBeforeLast(',')
}

private const val SECONDS_IN_YEAR: Long = 31536000
private const val SECONDS_IN_MONTH: Long = 2628000
private const val SECONDS_IN_WEEK: Long = 604800
private const val SECONDS_IN_DAY: Long = 86400
private const val SECONDS_IN_HOUR: Long = 3600
private const val SECONDS_IN_MINUTE: Long = 60

private val timePattern: Pattern = Pattern.compile(
  "\\s*((?<years>-?[0-9]*)\\s*[yY])?" +
    "\\s*((?<months>-?[0-9]*)\\s*M)?" +
    "\\s*((?<weeks>-?[0-9]*)\\s*[wW])?" +
    "\\s*((?<days>-?[0-9]*)\\s*[dD])?" +
    "\\s*((?<hours>-?[0-9]*)\\s*[hH])?" +
    "\\s*((?<minutes>-?[0-9]*)\\s*m)?" +
    "\\s*((?<seconds>-?[0-9]*)\\s*[sS])?\\s*",
)
