package eu.vitamoments.app.modules

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.Application
import eu.vitamoments.app.config.Config
import eu.vitamoments.app.data.tables.RefreshTokensTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.config.helpers.findProjectRoot
import eu.vitamoments.app.data.tables.TimeLinePostsTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.net.URI

fun Application.configureDatabase() {
    DatabaseFactory.init()
}

object DatabaseFactory {
    private val env by lazy {
        val rootDir = findProjectRoot()
        dotenv {
            directory = rootDir.absolutePath
            filename = ".env"
            ignoreIfMissing = false
        }
    }

    val db: Database by lazy {
        // 1) Haal DATABASE_URL op uit env of .env
        val rawDbUrl: String =
            System.getenv("DATABASE_URL")
                ?: env["DATABASE_URL"]
                ?: error("Env file must have DATABASE_URL")

        // 2) Als het een postgres:// URL is, gebruik Config.app.dbName als er nog geen path is
        val finalUri = if (rawDbUrl.startsWith("postgres://") || rawDbUrl.startsWith("postgresql://")) {
            val uri = URI(rawDbUrl)

            val hasDbInPath = !uri.path.isNullOrBlank() && uri.path != "/"

            val path = if (hasDbInPath) {
                // bv: postgres://user:pass@host:port/healthy_prod  -> gewoon gebruiken
                uri.path
            } else {
                // geen db in de URL → gebruik environment-specific dbName
                "/${Config.app.dbName}"
            }

            URI(
                uri.scheme,
                uri.userInfo,
                uri.host,
                if (uri.port != -1) uri.port else 5432,
                path,
                uri.query,
                uri.fragment
            )
        } else {
            URI(rawDbUrl)
        }

        val port = if (finalUri.port != -1) finalUri.port else 5432

        val username = finalUri.userInfo?.split(":")?.getOrNull(0) ?: error("No DB user in DATABASE_URL")
        val password = finalUri.userInfo?.split(":")?.getOrNull(1) ?: error("No DB password in DATABASE_URL")

        val jdbcUrl = "jdbc:postgresql://${finalUri.host}:$port${finalUri.path}"

        val database = Database.connect(
            url = jdbcUrl,
            driver = "org.postgresql.Driver",
            user = username,
            password = password
        )

        val tables = listOf(
            UsersTable,
            RefreshTokensTable,
            TimeLinePostsTable
        )

        transaction(database) {
            SchemaUtils.create(*tables.toTypedArray())
            SchemaUtils.createMissingTablesAndColumns(*tables.toTypedArray())
        }

        database
    }

    fun init(): Database = db
}
