package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.entity.DomainEntity
import org.anvilpowered.anvil.entity.ServerNode

interface ServerNodeRepositoryActions : RepositoryActions<ServerNode> {

    suspend fun DomainEntity.Repository<out ServerNode>.findByName(name: String): ServerNode?
}
