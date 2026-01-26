package org.anvilpowered.anvil.core.chat

import cats.effect.*
import cats.effect.std.{Console, Random}
import cats.syntax.all.*
import fs2.Stream
import net.kyori.adventure.text.Component
import org.http4s.*
import org.http4s.client.Client
import org.http4s.dsl.{Http4sDsl, Http4sDsl2}
import org.http4s.implicits.*
import org.typelevel.ci.*

import scala.concurrent.duration.*
import net.kyori.adventure.text.minimessage.MiniMessage

class HTTPAudienceIngress {
  def createService[F[_]: Async as F](audience: Audience): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]
    import dsl.*
    HttpRoutes.of[F] {
      case r @ POST -> Root / "sendMessage" => {
        r.as[String]

        Ok("test")
      }
      case POST -> Root / "send2" => Ok()
    }
  }
}

object ComponentVar {
  def unapply(str: String): Option[Component] = Some(MiniMessage.miniMessage().deserialize(str, Seq.empty*))
}
