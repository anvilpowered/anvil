package org.anvilpowered.anvil.api.command

import java.util.UUID

class CommandContext<T>(val source: T, var arguments: Array<String>, var userUUID: UUID)