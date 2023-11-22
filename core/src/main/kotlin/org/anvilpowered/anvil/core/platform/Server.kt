package org.anvilpowered.anvil.core.platform

import net.kyori.adventure.audience.Audience

interface Server {

    val platform: Platform

    val broadcastAudience: Audience

    val systemSubject: Audience
}
