package org.anvilpowered.anvil.db.datastore

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.sourcegrade.kontour.DomainEntity

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
