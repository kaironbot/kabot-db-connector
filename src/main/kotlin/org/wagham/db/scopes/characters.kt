package org.wagham.db.scopes

import org.bson.Document
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.exceptions.NoActiveCharacterException
import org.wagham.db.models.Character

suspend fun KabotMultiDBClient.getActiveCharacter(guildId: String, playerId: String): Character {
    return this.getGuildDb(guildId)?.let {
        val col = it.getCollection<Character>("characters")
        col.findOne(Document(mapOf("status" to "active", "player" to playerId)))
            ?: throw NoActiveCharacterException(playerId)
    } ?: throw InvalidGuildException(guildId)
}

fun KabotMultiDBClient.getAllCharacters(guildId: String) =
    this.getGuildDb(guildId)?.getCollection<Character>("characters")?.find("{}")?.toFlow() ?: throw InvalidGuildException(guildId)
