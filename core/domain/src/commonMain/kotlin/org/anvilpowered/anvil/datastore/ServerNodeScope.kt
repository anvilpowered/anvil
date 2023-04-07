package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.entity.ServerNode
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.SizedIterable
import org.sourcegrade.kontour.scope.CreateScope
import org.sourcegrade.kontour.scope.DeleteScope
import org.sourcegrade.kontour.scope.FindScope

interface ServerNodeScope :
    CreateScope<ServerNode, ServerNode.CreateDto>,
    FindScope<ServerNode>,
    DeleteScope<ServerNode> {

    fun DomainEntity.Repository<ServerNode>.findByGameType(): GameTypeJoin<SizedIterable<ServerNode>>
}
