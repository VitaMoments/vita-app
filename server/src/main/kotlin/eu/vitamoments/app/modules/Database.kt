package eu.vitamoments.app.modules

import eu.vitamoments.app.config.AppEnvironment
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.Application
import eu.vitamoments.app.config.Config
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.config.helpers.findProjectRoot
import eu.vitamoments.app.data.enums.UserRole.ADMIN
import eu.vitamoments.app.data.enums.UserRole.USER
import eu.vitamoments.app.data.tables.nevo.ProductsTable
import eu.vitamoments.app.dbHelpers.PasswordHasher
import eu.vitamoments.app.services.importNevoCsvIntoPostgres
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.net.URI
import java.nio.file.Paths
import java.util.UUID

fun Application.configureDatabase() {
    DatabaseFactory.init()
}

object DatabaseFactory {
    private val env by lazy {
        val rootDir = findProjectRoot()
        dotenv {
            directory = rootDir.absolutePath
            filename = ".env"
            ignoreIfMissing = true
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

//        val jdbcUrl = "jdbc:postgresql://${finalUri.host}:$port${finalUri.path}"

        val query = finalUri.query?.let { "?$it" } ?: ""
        val jdbcUrl = "jdbc:postgresql://${finalUri.host}:$port${finalUri.path}$query"


        runMigrations(jdbcUrl = jdbcUrl, username = username, password = password)

        val database = Database.connect(
            url = jdbcUrl,
            driver = "org.postgresql.Driver",
            user = username,
            password = password
        )

        if (Config.currentEnvironment == AppEnvironment.Dev) transaction(database) {
            seedUsers();
            seedNevoProducts()
        }
        database
    }

    private fun runMigrations(jdbcUrl: String, username: String, password: String) {
        Flyway.configure()
            .dataSource(jdbcUrl, username, password)
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .load()
            .migrate()
    }

    private fun seedUsers() {
        val hasAnyUser = UsersTable
            .selectAll()
            .limit(1)
            .any()

        if (hasAnyUser) return

        UsersTable.insert {
            it[id] = UUID.randomUUID()
            it[username] = "paccie"
            it[email] = "falcoberendhaus@gmail.com"
            it[alias] = "Falco"
            it[bio] = "Hello, I am Falco"
            it[imageUrl] = null
            it[role] = ADMIN
            it[password] = PasswordHasher.hashPassword("Falco123!")
        }

        for (i in 1..100) {
            UsersTable.insert {
                it[id] = UUID.randomUUID()
                it[username] = "user_$i"
                it[email] = "user_$i@vitamoments.eu"
                it[alias] = "User $i"
                it[bio] = "Hello, I am a Demo User $i. Welcome to my profile"
                it[imageUrl] = null
                it[role] = USER
                it[password] = PasswordHasher.hashPassword("User123!$i")
            }
        }

    }

    private fun seedNevoProducts() {
        val hasAnyProduct = ProductsTable.selectAll().limit(1).any()

        if (hasAnyProduct) return

        val csvPath: String =
            System.getenv("NEVO_CSV_PATH")
                ?: env["NEVO_CSV_PATH"]
                ?: error("Env file must have NEVO_CSV_PATH")

        importNevoCsvIntoPostgres(Paths.get(csvPath), wipeExisting = true)
    }

    fun init(): Database = db
}
