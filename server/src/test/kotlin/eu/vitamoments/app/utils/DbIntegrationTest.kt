package eu.vitamoments.app.utils

import eu.vitamoments.app.data.tables.RefreshTokensTable
import eu.vitamoments.app.data.tables.TimeLineItemsTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.modules.DatabaseFactory
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
            TimeLineItemsTable.deleteAll()
        }
    }
}