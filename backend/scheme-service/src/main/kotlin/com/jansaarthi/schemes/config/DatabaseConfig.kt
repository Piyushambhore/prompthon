package com.jansaarthi.schemes.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

/**
 * Initialises the HikariCP connection pool and creates tables if they
 * do not already exist. Separated from the repository layer so the
 * database implementation can be swapped without touching the API.
 *
 * Supports PostgreSQL as primary, with seamless fallback to an
 * embedded H2 database (in PostgreSQL mode) if PostgreSQL is not running locally.
 */
object DatabaseConfig {

    private val logger = LoggerFactory.getLogger(DatabaseConfig::class.java)

    fun init(databaseUrl: String, user: String, password: String) {
        val isExplicitH2 = databaseUrl.startsWith("jdbc:h2:")

        if (isExplicitH2) {
            logger.info("Initializing H2 database at $databaseUrl")
            connectAndCreate(databaseUrl, "org.h2.Driver", user, password, "TRANSACTION_READ_COMMITTED")
            return
        }

        // Parse and normalize cloud PostgreSQL connection URIs (e.g. Supabase, Neon, Render)
        var targetUrl = databaseUrl
        var targetUser = user
        var targetPassword = password

        if (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://")) {
            try {
                val uri = java.net.URI(databaseUrl)
                val userInfo = uri.userInfo
                if (!userInfo.isNullOrBlank()) {
                    val parts = userInfo.split(":")
                    if (targetUser.isBlank() || targetUser == "postgres") targetUser = parts[0]
                    if (parts.size > 1 && (targetPassword.isBlank() || targetPassword == "postgres")) targetPassword = parts[1]
                }
                val host = uri.host
                val port = if (uri.port != -1) ":${uri.port}" else ""
                val path = uri.path ?: ""
                val query = if (!uri.query.isNullOrBlank()) "?${uri.query}" else ""
                targetUrl = "jdbc:postgresql://$host$port$path$query"
            } catch (e: Exception) {
                targetUrl = "jdbc:postgresql://" + databaseUrl.removePrefix("postgres://").removePrefix("postgresql://")
            }
        } else if (!databaseUrl.startsWith("jdbc:")) {
            targetUrl = "jdbc:postgresql://$databaseUrl"
        }

        try {
            logger.info("Connecting to PostgreSQL at $targetUrl (user: $targetUser)")
            connectAndCreate(targetUrl, "org.postgresql.Driver", targetUser, targetPassword, "TRANSACTION_READ_COMMITTED")
            logger.info("Connected to PostgreSQL successfully")
        } catch (e: Exception) {
            logger.warn("PostgreSQL connection failed (${e.message ?: "Host unreachable"}).")
            val fallbackUrl = "jdbc:h2:./data/jansaarthi;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
            logger.info("Falling back to embedded H2 database at $fallbackUrl for zero-setup execution")
            connectAndCreate(fallbackUrl, "org.h2.Driver", "sa", "", "TRANSACTION_READ_COMMITTED")
            logger.info("Embedded database initialized successfully")
        }
    }

    private fun connectAndCreate(url: String, driver: String, u: String, p: String, isolation: String) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl              = url
            driverClassName      = driver
            username             = u
            password             = p
            maximumPoolSize      = 10
            isAutoCommit         = false
            transactionIsolation = isolation
            connectionTimeout    = 3000
            validate()
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(
                SchemesTable,
                EligibilityRulesTable,
                RequiredDocumentsTable
            )
        }
    }
}

