package org.anvilpowered.anvil.domain.user

/**
 * An online player.
 */
interface Player : UserFacetScope, Subject, Audience {

    val gameUser: GameUser
}
