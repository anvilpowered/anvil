package org.anvilpowered.anvil.domain.command

import org.anvilpowered.anvil.domain.user.Audience
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.anvil.domain.user.Subject
import org.anvilpowered.anvil.domain.user.User

interface CommandSource {

    /**
     * The [User] associated with the executed command, if any.
     */
    val user: User?

    val audience: Audience

    val subject: Subject

    /**
     * The [Player] associated with the executed command, if any.
     */
    val player: Player?

    /**
     * The [GameUser] user associated with the executed command, if any.
     */
    val gameUser: GameUser?
}
