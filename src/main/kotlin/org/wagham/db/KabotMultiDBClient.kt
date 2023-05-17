package org.wagham.db

import com.mongodb.reactivestreams.client.ClientSession
import io.kotest.common.runBlocking
import kotlinx.coroutines.flow.fold
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.reactivestreams.KMongo
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.exceptions.TransactionAbortedException
import org.wagham.db.models.MongoCredentials
import org.wagham.db.models.client.TransactionResult
import org.wagham.db.scopes.*

class KabotMultiDBClient(
    credentials: MongoCredentials
) {

    private val adminDatabase = KMongo.createClient(
        credentials.toConnectionString()
    ).coroutine.getDatabase(credentials.database)
    private val databaseCache: Map<String, CoroutineDatabase>
    private val clientCache: Map<String, CoroutineClient>

    val backgroundsScope = KabotDBBackgroundScope(this)
    val bountiesScope = KabotDBBountyScope(this)
    val buildingsScope = KabotDBBuildingScope(this)
    val featsScope = KabotDBFeatScope(this)
    val flameScope = KabotDBFlameScope(this)
    val charactersScope = KabotDBCharacterScope(this)
    val characterTransactionsScope = KabotDBCharacterTransactionsScope(this)
    val itemsScope = KabotDBItemScope(this)
    val playersScope = KabotDBPlayerScope(this)
    val proficiencyScope = KabotDBProficiencyScope(this)
    val raceScope = KabotDBRaceScope(this)
    val scheduledEventsScope = KabotDBScheduledEventsScope(this)
    val serverConfigScope = KabotDBServerConfigScope(this)
    val sessionScope = KabotDBSessionScope(this)
    val spellsScope = KabotDBSpellScope(this)
    val subclassesScope = KabotDBSubclassScope(this)
    val utilityScope = KabotDBUtilityScope(this)

    init {
        var initResult: Pair<Map<String, CoroutineClient>, Map<String, CoroutineDatabase>> = Pair(emptyMap(), emptyMap())
        runBlocking {
            initResult = adminDatabase.getCollection<MongoCredentials>("credentials")
                .find("{}").toFlow()
                .fold(Pair(emptyMap(), emptyMap())) { acc, guildCredentials ->
                    val client = KMongo.createClient(guildCredentials.toConnectionString()).coroutine
                    Pair(
                        acc.first + (guildCredentials.guildId to client),
                        acc.second + (guildCredentials.guildId to client.getDatabase(guildCredentials.database))
                    )
                }
        }
        clientCache = initResult.first
        databaseCache = initResult.second
    }

    fun getGuildDb(guildId: String): CoroutineDatabase = databaseCache[guildId] ?: throw InvalidGuildException(guildId)

    suspend fun transaction(guildId: String, block: suspend (ClientSession) -> Boolean): TransactionResult {
        if (clientCache[guildId] == null) throw InvalidGuildException(guildId)
        return clientCache[guildId]!!.startSession().use {
            it.startTransaction()
            try {
                if (block(it)) {
                    it.commitTransactionAndAwait()
                    TransactionResult(true)
                }
                else throw TransactionAbortedException()
            } catch (e: Exception) {
                TransactionResult(
                    false,
                    e
                )
            }
        }
    }
}