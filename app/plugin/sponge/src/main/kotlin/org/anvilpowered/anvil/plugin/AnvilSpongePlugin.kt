package org.anvilpowered.anvil.plugin

import com.mojang.brigadier.tree.LiteralCommandNode
import org.anvilpowered.anvil.sponge.AnvilSpongeApi
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent

context(AnvilSpongeApi)
class AnvilSpongePlugin : AnvilPlugin() {
    fun registerCommands(event: RegisterCommandEvent<LiteralCommandNode<*>>) {

    }
}
