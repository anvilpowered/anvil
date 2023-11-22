package org.anvilpowered.anvil.plugin

import com.mojang.brigadier.tree.LiteralCommandNode
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent

class AnvilSpongePlugin(private val plugin: AnvilPlugin) {
    fun registerCommands(event: RegisterCommandEvent<LiteralCommandNode<*>>) {
    }
}
