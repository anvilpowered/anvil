package org.anvilpowered.anvil.config

import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import net.kyori.adventure.text.Component
import java.lang.reflect.Type
import net.kyori.adventure.text.minimessage.MiniMessage

object MiniMessageSerializer {
  given typeSerializer: TypeSerializer[Component] with {
    override def deserialize(`type`: Type, node: ConfigurationNode): Component =
      MiniMessage.miniMessage().deserialize(node.getString(), Seq.empty*)
    override def serialize(`type`: Type, obj: Component, node: ConfigurationNode): Unit =
      node.set(MiniMessage.miniMessage().serialize(obj))
  }
}
