package org.anvilpowered.anvil.web

import org.anvilpowered.anvil.datastore.GameTypeRepositoryActions
import org.anvilpowered.anvil.entity.GameType
import java.util.UUID

suspend fun GameTypeRepositoryActions.bar() {
    GameType.findById(UUID.randomUUID())
}
