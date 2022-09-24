package org.wagham.db

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
    credentials: Map<String, MongoCredentials>
) {

    private val databaseCache = credentials.keys.fold(mapOf<String, CoroutineDatabase>()) { acc, guildId ->
        credentials[guildId]?.let {
            acc + (guildId to
                    KMongo.createClient("mongodb://${it.username}:${it.password}@${it.ip}:${it.port}/${it.database}").coroutine.getDatabase(it.database))
        } ?: throw InvalidCredentialsExceptions(guildId)
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