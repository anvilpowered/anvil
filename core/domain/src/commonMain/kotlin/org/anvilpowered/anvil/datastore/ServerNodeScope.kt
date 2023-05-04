package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.entity.ServerNode
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.SizedIterable
import org.sourcegrade.kontour.scope.CrudScope

interface ServerNodeScope : CrudScope<ServerNode, ServerNode.CreateDto> {

    fun DomainEntity.Repository<ServerNode>.findByGameType(): GameTypeJoin<SizedIterable<ServerNode>>
}
