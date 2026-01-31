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

import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.chat.SignedMessage
import java.time.Instant
import net.kyori.adventure.chat.SignedMessage.Signature
import java.util.Base64
import net.kyori.adventure.identity.Identity
import java.util.UUID
import cats.syntax.all.*
// import io.circe.syntax.*
// import io.circe.generic.auto.*

object MiniMessageCodec {
  given codec: Codec[Component] = Codec.from(
    Decoder.decodeString.map { MiniMessage.miniMessage().deserialize(_, Seq.empty*) },
    Encoder.encodeString.contramap { MiniMessage.miniMessage().serialize(_) },
  )
  case class SignedMessageImpl(
      override val identity: Identity,
      override val timestamp: Instant,
      override val salt: Long,
      override val signature: Signature,
      override val unsignedContent: Component,
      override val message: String,
  ) extends SignedMessage

  case class SignatureImpl(override val bytes: Array[Byte]) extends Signature

  given uuidCodec: Codec[UUID] = Codec.from(Decoder.decodeUUID, Encoder.encodeUUID)

  given identityCodec: Codec[Identity] = Codec[UUID].imap(Identity.identity)(_.uuid)

  given signatureCodec: Codec[Signature] = Codec.from(
    Decoder.decodeString.emap { str =>
      try {
        Right(SignatureImpl(Base64.getDecoder.decode(str)))
      } catch {
        case e: IllegalArgumentException =>
          Left(s"Invalid Base64 string: ${e.getMessage}")
      }
    },
    Encoder.encodeString.contramap[Signature](sig => Base64.getEncoder.encodeToString(sig.bytes)),
  )
  given signedCodec: Codec[SignedMessage] = Codec.from(
    Decoder.forProduct6("identity", "timestamp", "salt", "signature", "unsignedContent", "message") {
      (identity: Identity, timestamp: Instant, salt: Long, signature: Signature, unsignedContent: Component, message: String) =>
        SignedMessageImpl(identity, timestamp, salt, signature, unsignedContent, message)
    },
    Encoder.forProduct6("identity", "timestamp", "salt", "signature", "unsignedContent", "message") { msg =>
      (msg.identity, msg.timestamp, msg.salt, msg.signature, msg.unsignedContent, msg.message)
    },
  )
}
