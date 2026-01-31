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

package org.anvilpowered.anvil.chat

import cats.data.EitherT
import cats.effect.*
import cats.effect.std.{Console, Random}
import cats.syntax.all.*
import fs2.Stream
import io.circe.Codec
import io.circe.generic.auto.*
import io.circe.syntax.*
import net.kyori.adventure.chat.{ChatType, SignedMessage}
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.http4s.*
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.dsl.{Http4sDsl, Http4sDsl2}
import org.http4s.implicits.*
import org.typelevel.ci.*

import scala.concurrent.duration.*

class HTTPAudienceIngress {
  case class SendMessageRequest(message: Component, boundChatType: Option[ChatType.Bound])
  object SendMessageRequest {
    import MiniMessageCodec.given
    import ChatTypeCodec.given
    given decoder[F[_]: Concurrent]: EntityDecoder[F, SendMessageRequest] = jsonOf[F, SendMessageRequest]
  }
  case class SendSignedMessageRequest(message: SignedMessage, boundChatType: ChatType.Bound)
  object SendSignedMessageRequest {
    import MiniMessageCodec.given
    import ChatTypeCodec.given
    given decoder[F[_]: Concurrent]: EntityDecoder[F, SendSignedMessageRequest] = jsonOf[F, SendSignedMessageRequest]
  }
  given componentDecoder[F[_]: Async as F]: EntityDecoder[F, Component] with {
    override def decode(m: Media[F], strict: Boolean): DecodeResult[F, Component] = EitherT.liftF(
      for {
        str <- m.as[String]
        result <- F.delay { MiniMessage.miniMessage().deserialize(str, Seq.empty*) }
      } yield result,
    )
    override def consumes: Set[MediaRange] = Set(MediaRange.`text/*`)
  }
  def createService[F[_]: Async as F](audience: Audience): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]
    import dsl.*
    HttpRoutes.of[F] {
      case r @ POST -> Root / "sendMessage" => {
        for {
          request <- r.as[SendMessageRequest]
          a <- request.boundChatType match {
            case Some(bound) => audience.sendMessage(request.message, bound)
            case None => audience.sendMessage(request.message)
          }
          resp <-
            if (a) {
              Ok("Sent Message")
            } else {
              InternalServerError("Could not decode message")
            }

        } yield resp
      }
      case POST -> Root / "sendSignedMessage" => Ok()
    }
  }
}

object ComponentVar {
  def unapply(str: String): Option[Component] = Some(MiniMessage.miniMessage().deserialize(str, Seq.empty*))
}
