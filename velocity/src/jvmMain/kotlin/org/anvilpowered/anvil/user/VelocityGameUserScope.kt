package org.anvilpowered.anvil.user

import com.velocitypowered.api.proxy.ProxyServer
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.anvil.domain.user.Subject
import kotlin.jvm.optionals.getOrNull

class VelocityGameUserScope(private val proxyServer: ProxyServer) : GameUser.GamePlatformScope {
    override val GameUser.subject: Subject?
        get() = proxyServer.getPlayer(id).getOrNull()?.toAnvilSubject()
    override val GameUser.player: Player?
        get() = proxyServer.getPlayer(id).getOrNull()?.toAnvilPlayer()
}
