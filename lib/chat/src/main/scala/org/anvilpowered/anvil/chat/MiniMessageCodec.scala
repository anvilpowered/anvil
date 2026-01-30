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

import io.circe.{Codec, Decoder, Encoder}
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer

import java.lang.reflect.Type

object MiniMessageCodec {
  given codec: Codec[Component] = Codec.from(
    Decoder.decodeString.map { MiniMessage.miniMessage().deserialize(_, Seq.empty*) },
    Encoder.encodeString.contramap { MiniMessage.miniMessage().serialize(_) },
  )

  given typeSerializer: TypeSerializer[Component] with {
    override def deserialize(`type`: Type, node: ConfigurationNode): Component =
      MiniMessage.miniMessage().deserialize(node.getString(), Seq.empty*)
    override def serialize(`type`: Type, obj: Component, node: ConfigurationNode): Unit =
      node.set(MiniMessage.miniMessage().serialize(obj))
  }
}
