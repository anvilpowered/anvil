package org.anvilpowered.anvil.domain.command

import org.anvilpowered.anvil.domain.user.Audience
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.PermissionSubject
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.anvil.domain.user.User

interface CommandSource {

    val audience: Audience

    val permissionSubject: PermissionSubject

    /**
     * The [Player] associated with the executed command, if any.
     */
    val player: Player?

    /**
     * The [User] associated with the executed command, if any.
     */
    val user: User?

    /**
     * The [GameUser] user associated with the executed command, if any.
     */
    val gameUser: GameUser?
}
