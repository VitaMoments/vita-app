package eu.vitamoments.app.dbHelpers

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

//suspend fun <T> dbQuery(block: suspend () -> T): T =
//    suspendTransaction { block() }

//suspend fun <T> dbQuery(block: () -> T): T =
//    withContext(Dispatchers.IO) {
//        transaction { block() }
//    }

