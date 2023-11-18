package org.anvilpowered.anvil.core.platform

import net.kyori.adventure.audience.Audience

interface Server {

    val platform: Platform

    val systemSubject: Audience

    interface Scope {
        val server: Server
    }
}
