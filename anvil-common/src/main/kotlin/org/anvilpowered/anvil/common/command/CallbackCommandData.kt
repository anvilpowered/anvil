package org.anvilpowered.anvil.common.command

import com.google.inject.Singleton
import java.util.UUID

@Singleton
open class CallbackCommandData<TCommandSource> {

  val callbacks: MutableMap<UUID, (TCommandSource) -> Unit> = mutableMapOf()
}
