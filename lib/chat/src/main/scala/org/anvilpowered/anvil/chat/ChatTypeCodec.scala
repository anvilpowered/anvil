package org.anvilpowered.anvil.chat

import io.circe.Codec
import net.kyori.adventure.chat.ChatType

object ChatTypeCodec {
  given codec: Codec[ChatType] = Codec.from(
    
  )
}
