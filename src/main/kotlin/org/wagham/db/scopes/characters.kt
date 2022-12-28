package org.wagham.db.scopes

import org.bson.Document
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.exceptions.NoActiveCharacterException
import org.wagham.db.models.Character
import org.wagham.db.pipelines.characters.CharacterWithPlayer


class KabotDBCharacterScope(
    private val client: KabotMultiDBClient
) {

    suspend fun getActiveCharacter(guildId: String, playerId: String): Character {
        return client.getGuildDb(guildId)?.let {
            val col = it.getCollection<Character>("characters")
            col.findOne(Document(mapOf("status" to "active", "player" to playerId)))
                ?: throw NoActiveCharacterException(playerId)
        } ?: throw InvalidGuildException(guildId)
    }

    fun getAllCharacters(guildId: String, status: CharacterStatus? = null) =
        client.getGuildDb(guildId)?.getCollection<Character>("characters")
            ?.find(Document(
                status?.let {
                    mapOf("status" to status)
                } ?: emptyMap<String, String>()
            ))
            ?.toFlow()
            ?: throw InvalidGuildException(guildId)

    fun getCharactersWithPlayer(guildId: String, status: CharacterStatus? = null) =
        client.getGuildDb(guildId)
            ?.getCollection<Character>("characters")
            ?.aggregate<CharacterWithPlayer>(CharacterWithPlayer.getPipeline(status))
            ?.toFlow()
            ?: throw InvalidGuildException(guildId)

}
