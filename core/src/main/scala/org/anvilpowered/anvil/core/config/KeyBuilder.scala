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

package org.anvilpowered.anvil.core.config
import cats.Monoid
import io.circe.generic.semiauto.*
import io.circe.{Codec, Decoder, Encoder}
import io.leangen.geantyref.TypeToken

import scala.annotation.tailrec
import scala.quoted.*
import scala.reflect.ClassTag

inline def createKey[T](fallback: T, description: Option[String] = None)(using tag: ClassTag[T], codec: Codec[T], monoid: Monoid[T]): Key[T] =
  ${ createKeyImpl('tag, 'codec, 'monoid, 'fallback, 'description) }

def createKeyImpl[T: Type](
    tag: Expr[ClassTag[T]],
    codec: Expr[Codec[T]],
    monoid: Expr[Monoid[T]],
    fallback: Expr[T],
    description: Expr[Option[String]],
)(using Quotes): Expr[Key[T]] = {
  import quotes.reflect.*

  @tailrec
  def findEnclosingValName(sym: Symbol): String = sym.tree match {
    case ValDef(name, _, _)                                   => name
    case DefDef(name, _, _, _) if name.startsWith("$anonfun") =>
      // anonymous function, go up
      findEnclosingValName(sym.owner)
    case _ =>
      sym.owner match {
        case owner if owner != Symbol.noSymbol => findEnclosingValName(owner)
        case _                                 => "unknown"
      }
  }

  val name = findEnclosingValName(Symbol.spliceOwner)

  '{
    Key(${ Expr(name) }, Key.typeTokenOf[T], $codec, $monoid, $fallback, $description)
  }
}
