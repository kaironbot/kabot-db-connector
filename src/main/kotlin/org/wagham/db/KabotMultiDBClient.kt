package org.wagham.db

import io.kotest.common.runBlocking
import kotlinx.coroutines.flow.fold
import org.bson.Document
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.wagham.db.exceptions.InvalidCredentialsExceptions
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.exceptions.NoActiveCharacterException
import org.wagham.db.models.Item
import org.wagham.db.models.MongoCredentials

class KabotMultiDBClient(
    credentials: MongoCredentials
) {

    private val adminDatabase = KMongo.createClient(
        credentials.toConnectionString()
    ).coroutine.getDatabase(credentials.database)
    private lateinit var databaseCache: Map<String, CoroutineDatabase>
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


    suspend fun getActiveCharacter(guildId: String, playerId: String): org.wagham.db.models.Character {
        return databaseCache[guildId]?.let {
            val col = it.getCollection<org.wagham.db.models.Character>("characters")
            col.findOne(Document(mapOf("status" to "active", "player" to playerId)))
                ?: throw NoActiveCharacterException(playerId)
        } ?: throw InvalidGuildException(guildId)
    }

    fun getItems(guildId: String) =
        databaseCache[guildId]?.getCollection<Item>("items")?.find("{}")?.toFlow() ?: throw InvalidGuildException(guildId)

}