package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.entity.DomainEntity
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

internal suspend fun <T : Table, E : DomainEntity> newSaveTransaction(
    table: T,
    toTable: context(T) InsertStatement<Number>.() -> Unit,
    toEntity: ResultRow.() -> E,
): E {
    val fromDB = newSuspendedTransaction {
        table.insert { toTable(it) }
    }.resultedValues?.firstOrNull()?.toEntity()
    checkNotNull(fromDB) { "Failed to save entity" }
    return fromDB
}
