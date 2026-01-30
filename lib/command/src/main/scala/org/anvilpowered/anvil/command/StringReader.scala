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

package org.anvilpowered.anvil.command

import cats.data.{EitherT, StateT}
import cats.{Applicative, Monad, data}
import org.anvilpowered.anvil.command.exception.{ArgumentError, CommandSyntaxException, ParseError}

import java.lang
import scala.annotation.tailrec
import scala.util.boundary
import scala.util.boundary.break

trait StringReader {

  /** The underlying string.
    */
  val string: String

  /** The current cursor position.
    */
  val cursor: Int

  /** The number of characters remaining that may be read.
    */
  def remainingLength: Int = totalLength - cursor

  /** The total number of characters in the string.
    */
  def totalLength: Int = string.length

  /** The substring from the start of the string to the current cursor position.
    */
  def previous: String = string.substring(0, cursor)

  /** The substring from the current cursor position to the end of the string.
    */
  def remaining: String = string.substring(cursor)

  /** Whether [length] number of characters can be read.
    */
  def canRead(length: Int = 1): Boolean = cursor + length <= totalLength

  /** Returns the character at the current cursor position + [offset] but does not advance the cursor.
    */
  def peek(offset: Int = 0): Char = string(cursor + offset)

  def peekSlice(length: Int): String = string.substring(cursor, cursor + length)
}

object StringReader {
  type ParseT[F[_], T] = StateT[[X] =>> EitherT[F, ParseError, X], StringReader, T]
  val ParseT: StateT.type = StateT

  extension (reader: StringReader) {
    def advanceCursor(amount: Int): StringReader = new StringReader {
      override val string: String = reader.string
      override val cursor: Int = reader.cursor + amount
    }
    def withCursor(pos: Int): StringReader = new StringReader {
      override val string: String = reader.string
      override val cursor: Int = pos
    }

    private def skipWhile(predicate: Char => Boolean): Int = {
      @tailrec def doSkip(offset: Int): Int =
        if (reader.canRead(offset) && predicate(reader.peek(offset))) doSkip(offset + 1)
        else offset

      doSkip(0)
    }
  }

  private def isAllowedNumber(c: Char): Boolean =
    c >= '0' && c <= '9' || c == '.' || c == '-'

  private def isQuotedStringStart(c: Char): Boolean =
    c == '"' || c == '\''

  private def isAllowedInUnquotedString(c: Char): Boolean =
    c >= '0' && c <= '9'
      || c >= 'A' && c <= 'Z'
      || c >= 'a' && c <= 'z'
      || c == '_' || c == '-'
      || c == '.' || c == '+'

  /* Data Parsing */
  private def readNumber[F[_]: Monad, T](numType: String)(parse: String => T): ParseT[F, T] =
    StateT { reader =>
      val numOffset = reader.skipWhile(isAllowedNumber)
      val number = reader.string.substring(reader.cursor, reader.cursor + numOffset)
      try {
        EitherT.pure(reader.advanceCursor(numOffset), parse(number))
      } catch {
        case _: NumberFormatException =>
          EitherT.leftT(ParseError(number, s"is not a valid $numType"))
      }
    }

  def readInt[F[_]: Monad]: ParseT[F, Int] = readNumber("integer")(lang.Integer.parseInt)
  def readLong[F[_]: Monad]: ParseT[F, Long] = readNumber("long")(lang.Long.parseLong)
  def readFloat[F[_]: Monad]: ParseT[F, Float] = readNumber("float")(lang.Float.parseFloat)
  def readDouble[F[_]: Monad]: ParseT[F, Double] = readNumber("double")(lang.Double.parseDouble)

  def readBoolean[F[_]: Monad]: ParseT[F, Boolean] =
    for {
      value <- readString
      result <- StateT.liftF {
        value match {
          case "true"  => EitherT.pure(true)
          case "false" => EitherT.pure(false)
          case _       => EitherT.leftT(ParseError(value, "expected bool"))
        }
      }
    } yield result

  def readStringUntil[F[_]: Monad](terminator: Char, startOffset: Int = 0): ParseT[F, String] = StateT { reader =>
    val sb = new StringBuilder
    var escaped = false
    var offset = startOffset
    boundary {
      while (reader.canRead(offset)) {
        val c = reader.peek(offset)
        if (escaped) {
          if (c == terminator || c == '\\') {
            sb.addOne(c)
            escaped = false
          } else {
            break(EitherT.leftT(ParseError(reader.string, "invalid escape")))
          }
        } else if (c == '\\') {
          escaped = true
        } else if (c == terminator) {
          break(EitherT.pure(reader.advanceCursor(offset), sb.toString()))
        }
        offset += 1
      }
      EitherT.leftT(ParseError(reader.string, "expected end of quote"))
    }
  }

  def readQuotedString[F[_]: Monad]: ParseT[F, String] = StateT { reader =>
    if (!reader.canRead()) {
      EitherT.pure(reader, "")
    } else {
      val next = reader.peek()
      if (!isQuotedStringStart(next)) {
        EitherT.leftT(ParseError(reader.string, "expected quote"))
      } else {
        readStringUntil(next).run(reader)
      }
    }
  }

  def readUnquotedString[F[_]: Monad]: ParseT[F, String] = StateT { reader =>
    val offset = reader.skipWhile(isAllowedInUnquotedString)
    EitherT.pure(reader.advanceCursor(offset), reader.peekSlice(offset))
  }

  def readRemainingString[F[_]: Monad]: ParseT[F, String] = StateT { reader =>
    EitherT.pure(reader.advanceCursor(reader.remainingLength), reader.remaining)
  }

  def readString[F[_]: Monad]: ParseT[F, String] =
    for {
      reader <- StateT.get[[A] =>> EitherT[F, ParseError, A], StringReader]
      result <-
        if (!reader.canRead()) StateT.pure[[A] =>> EitherT[F, ParseError, A], StringReader, String]("")
        else if (isQuotedStringStart(reader.peek())) readStringUntil(reader.peek(), 1)
        else readUnquotedString[F]
    } yield result
}
