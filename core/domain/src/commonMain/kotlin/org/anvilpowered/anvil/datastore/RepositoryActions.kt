package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.entity.DomainEntity

interface RepositoryActions<T : DomainEntity> {

    suspend fun DomainEntity.Repository<T>.save(item: T): T

    suspend fun DomainEntity.Repository<T>.findById(uuid: UUID): T?

    suspend fun DomainEntity.Repository<*>.deleteById(uuid: UUID): Boolean
}
