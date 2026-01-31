package org.anvilpowered.anvil.chat

import io.circe.Codec
import net.kyori.adventure.chat.ChatType
import io.circe.Decoder
import net.kyori.adventure.key.Key
import io.circe.Encoder
import cats.syntax.all.*
import net.kyori.adventure.text.Component

object ChatTypeCodec {
  import MiniMessageCodec.codec
  given codec: Codec[ChatType] = KeyCodec.codec.imap(ChatType.chatType)(_.key)
  given codecBound: Codec[ChatType.Bound] = Codec.from(
    Decoder.forProduct3("type", "name", "target") { (chatType: ChatType, name: Component, target: Option[Component]) =>
      chatType.bind(name, target.orNull)
    },
    Encoder.forProduct3("type", "name", "target") { bound =>
      (bound.`type`, bound.name, Option(bound.target))
    },
  )
}

object KeyCodec {
  given codec: Codec[Key] = Codec.from(
    Decoder.decodeString.map(Key.key),
    Encoder.encodeString.contramap(_.toString),
  )
}
