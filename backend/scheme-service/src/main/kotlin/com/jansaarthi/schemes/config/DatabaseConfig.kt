package com.jansaarthi.schemes.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Initialises the HikariCP connection pool and creates tables if they
 * do not already exist.  Separated from the repository layer so the
 * database implementation can be swapped without touching the API.
 */
object DatabaseConfig {

    fun init(databaseUrl: String, user: String, password: String) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl            = databaseUrl
            driverClassName    = "org.postgresql.Driver"
            username           = user
            this.password      = password
            maximumPoolSize    = 10
            isAutoCommit       = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        Database.connect(HikariDataSource(hikariConfig))

        // Create tables on first run — idempotent
        transaction {
            SchemaUtils.create(
                SchemesTable,
                EligibilityRulesTable,
                RequiredDocumentsTable
            )
        }
    }
}
