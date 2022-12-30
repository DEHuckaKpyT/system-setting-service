package com.example.plugins

import com.example.models.SystemSettings
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


/**
 * Created on 29.11.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Application.configureDatabase() {

    val config = environment.config

    fun connect() {
        val hikariConfig = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = config.property("database.url").getString()
            username = config.property("database.username").getString()
            password = config.property("database.password").getString()
            maximumPoolSize = 4
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
            validate()
        }

        Database.connect(HikariDataSource(hikariConfig))
    }

    fun createTables() {
        transaction {
            SchemaUtils.create(
                SystemSettings
            )
        }
    }

    connect()
    createTables()
}

suspend fun <T> executeQuery(block: suspend () -> T): T =
    withContext(Dispatchers.IO) {
        newSuspendedTransaction {
//            addLogger(StdOutSqlLogger)
            block()
        }
    }

/**
 * Example:
 * val item = ...
 * MyTable.upsert {
 * 	it[id] = item.id
 *	it[value1] = item.value1
 * }
 */

fun <T : Table> T.upsert(
    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
    body: T.(InsertStatement<Number>) -> Unit
) =
    InsertOrUpdate<Number>(this, keys = keys).apply {
        body(this)
        execute(TransactionManager.current())
    }

class InsertOrUpdate<Key : Any>(
    table: Table,
    isIgnore: Boolean = false,
    private vararg val keys: Column<*>
) : InsertStatement<Key>(table, isIgnore) {
    override fun prepareSQL(transaction: Transaction): String {
        val tm = TransactionManager.current()
        val updateSetter = (table.columns - keys).joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction)} $onConflict"
    }
}

/**
 * Example:
 * val items = listOf(...)
 * MyTable.batchUpsert(items) { table, item  ->
 * 	table[id] = item.id
 *	table[value1] = item.value1
 * }
 */

fun <T : Table, E> T.batchUpsert(
    data: Collection<E>,
    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
    body: T.(BatchInsertStatement, E) -> Unit
) =
    BatchInsertOrUpdate(this, keys = keys).apply {
        data.forEach {
            addBatch()
            body(this, it)
        }
        execute(TransactionManager.current())
    }

class BatchInsertOrUpdate(
    table: Table,
    isIgnore: Boolean = false,
    private vararg val keys: Column<*>
) : BatchInsertStatement(table, isIgnore, shouldReturnGeneratedValues = false) {
    override fun prepareSQL(transaction: Transaction): String {
        val tm = TransactionManager.current()
        val updateSetter = (table.columns - keys).joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction)} $onConflict"
    }
}