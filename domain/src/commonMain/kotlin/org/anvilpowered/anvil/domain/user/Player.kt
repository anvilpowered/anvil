package org.anvilpowered.anvil.domain.user

/**
 * An online player.
 */
interface Player : UserFacet, Subject, Audience {

    val gameUser: GameUser
}
