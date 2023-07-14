package org.anvilpowered.anvil.user

import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.anvil.domain.user.Subject
import org.spongepowered.api.Sponge
import kotlin.jvm.optionals.getOrNull

class SpongeGameUserScope : GameUser.GamePlatformScope {
    init {
    }
    override val GameUser.subject: Subject?
        get() = Sponge.server().player(id).getOrNull()?.toAnvilSubject()
    override val GameUser.player: Player?
        get() = Sponge.server().player(id).getOrNull()?.toAnvilPlayer()
}
