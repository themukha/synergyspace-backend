package io.synergyspace.common.infrastructure

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.CoroutineContext

object DatabaseFactory {

    fun init(config: ApplicationConfig) {
        println(config.toString())
        val driverClassName  = config.property("ktor.database.driverClassName").getString()
        val jdbcURL = config.property("ktor.database.jdbcURL").getString()
        val user = config.property("ktor.database.user").getString()
        val password = config.property("ktor.database.password").getString()
        val maxPoolSize = config.property("ktor.database.maximumPoolSize").getString().toInt()
        val autoCommit = config.property("ktor.database.isAutoCommit").getString().toBoolean()
        val transactionIsolation = config.property("ktor.database.transactionIsolation").getString()

        val hikariConfig = HikariConfig().apply {
            this.driverClassName = driverClassName
            this.jdbcUrl = jdbcURL
            this.username = user
            this.password = password
            this.maximumPoolSize = maxPoolSize
            this.isAutoCommit = autoCommit
            this.transactionIsolation = transactionIsolation
            validate()
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
    }

    suspend fun <T> dbQuery(
        context: CoroutineContext = Dispatchers.IO,
        block: suspend () -> T
    ): T = newSuspendedTransaction(context) {
        block()
    }
}