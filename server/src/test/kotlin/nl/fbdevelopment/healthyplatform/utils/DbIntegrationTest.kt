package nl.fbdevelopment.healthyplatform.utils

import nl.fbdevelopment.healthyplatform.config.JWTConfigLoader
import nl.fbdevelopment.healthyplatform.data.entities.RefreshTokenEntity
import nl.fbdevelopment.healthyplatform.data.entities.UserEntity
import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
import nl.fbdevelopment.healthyplatform.data.tables.RefreshTokensTable
import nl.fbdevelopment.healthyplatform.data.tables.TimeLinePostsTable
import nl.fbdevelopment.healthyplatform.data.tables.UsersTable
import nl.fbdevelopment.healthyplatform.dbHelpers.PasswordHasher
import nl.fbdevelopment.healthyplatform.dbHelpers.dbQuery
import nl.fbdevelopment.healthyplatform.modules.DatabaseFactory
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DbIntegrationTest {

    @BeforeAll
    fun initDatabase() {
        DatabaseFactory.init()
    }

    @BeforeEach
    fun cleanDatabase() {
        transaction(DatabaseFactory.db) {
            RefreshTokensTable.deleteAll()
            UsersTable.deleteAll()
            TimeLinePostsTable.deleteAll()
        }
    }
}