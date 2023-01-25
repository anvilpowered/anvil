package org.anvilpowered.anvil.ui.page.servers.create

import org.reduxkotlin.Store
import org.reduxkotlin.createStore

data class CreateState(
    val connectionString: String? = null,
    val connectionStringCopied: Boolean = false,
)

sealed interface CreateAction
data class GenerateConnectionStringAction(val connectionString: String) : CreateAction
object ConnectionStringCopied : CreateAction

private fun reducer(state: CreateState, action: Any): CreateState {
    action as CreateAction
    return when (action) {
        is GenerateConnectionStringAction -> state.copy(connectionString = action.connectionString)
        ConnectionStringCopied -> state.copy(connectionStringCopied = true)
    }
}

val createStore: Store<CreateState> = createStore(::reducer, CreateState())
