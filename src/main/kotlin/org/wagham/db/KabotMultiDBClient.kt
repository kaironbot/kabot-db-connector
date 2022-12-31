package org.wagham.db

import io.kotest.common.runBlocking
import kotlinx.coroutines.flow.fold
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.wagham.db.models.MongoCredentials
import org.wagham.db.scopes.*

class KabotMultiDBClient(
    credentials: MongoCredentials
) {

    private val adminDatabase = KMongo.createClient(
        credentials.toConnectionString()
    ).coroutine.getDatabase(credentials.database)
    private lateinit var databaseCache: Map<String, CoroutineDatabase>

    val backgroundsScope = KabotDBBackgroundScope(this)
    val bountiesScope = KabotDBBountyScope(this)
    val buildingsScope = KabotDBBuildingScope(this)
    val featsScope = KabotDBFeatScope(this)
    val flameScope = KabotDBFlameScope(this)
    val charactersScope = KabotDBCharacterScope(this)
    val itemsScope = KabotDBItemScope(this)
    val playersScope = KabotDBPlayerScope(this)
    val raceScope = KabotDBRaceScope(this)
    val serverConfigScope = KabotDBServerConfigScope(this)
    val spellsScope = KabotDBSpellScope(this)
    val subclassesScope = KabotDBSubclassScope(this)
    val utilityScope = KabotDBUtilityScope(this)

    init {
        runBlocking {
            databaseCache = adminDatabase.getCollection<MongoCredentials>("credentials")
                .find("{}").toFlow()
                .fold(mapOf()) { acc, guildCredentials ->
                    acc + (guildCredentials.guildId to
                            KMongo.createClient(guildCredentials.toConnectionString()).coroutine.getDatabase(guildCredentials.database))

                }
        }
    }

    fun getGuildDb(guildId: String): CoroutineDatabase? = databaseCache[guildId]
}