package org.anvilpowered.anvil.domain

import org.anvilpowered.anvil.domain.user.GameUser

/**
 * The union of all scopes a platform should provide.
 */
interface GamePlatformScope : GameUser.GamePlatformScope {
    companion object
}
