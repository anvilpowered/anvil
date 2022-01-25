package org.anvilpowered.anvil.api.command

class CommandContext<T>(val source: T, var arguments: Array<String>)