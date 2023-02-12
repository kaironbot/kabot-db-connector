package org.wagham.db.scopes

import com.mongodb.reactivestreams.client.ClientSession
import org.bson.Document
import org.litote.kmongo.*
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.exceptions.NoActiveCharacterException
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.Character
import org.wagham.db.pipelines.characters.CharacterWithPlayer


class KabotDBCharacterScope(
    private val client: KabotMultiDBClient
) {

    suspend fun getActiveCharacter(guildId: String, playerId: String): Character {
        return client.getGuildDb(guildId).let {
            val col = it.getCollection<Character>("characters")
            col.findOne(Character::status eq CharacterStatus.active, Character::player eq playerId)
                ?: throw NoActiveCharacterException(playerId)
        }
    }

    suspend fun getCharacter(guildId: String, characterName: String): Character =
        client.getGuildDb(guildId)
            .getCollection<Character>("characters")
            .findOne(Character::name eq characterName)
            ?: throw ResourceNotFoundException(characterName, "characters")

    suspend fun getCharacter(session: ClientSession, guildId: String, characterName: String): Character =
        client.getGuildDb(guildId)
            .getCollection<Character>("characters")
            .findOne(session, Character::name eq characterName)
            ?: throw ResourceNotFoundException(characterName, "characters")


    fun getAllCharacters(guildId: String, status: CharacterStatus? = null) =
        client.getGuildDb(guildId).getCollection<Character>("characters")
            .find(Document(
                status?.let {
                    mapOf("status" to status)
                } ?: emptyMap<String, String>()
            ))
            .toFlow()

    suspend fun addProficiencyToCharacter(guildId: String, characterName: String, proficiency: String) =
        client.getGuildDb(guildId)
            .getCollection<Character>("characters")
            .updateOne(
                Character::name eq characterName,
                addToSet(Character::proficiencies, proficiency)
            ).modifiedCount == 1L

    suspend fun addProficiencyToCharacter(session: ClientSession, guildId: String, characterName: String, proficiency: String) =
        client.getGuildDb(guildId)
            .getCollection<Character>("characters")
            .updateOne(
                session,
                Character::name eq characterName,
                addToSet(Character::proficiencies, proficiency)
            ).modifiedCount == 1L

    suspend fun removeProficiencyFromCharacter(guildId: String, characterName: String, proficiency: String) =
        client.getGuildDb(guildId)
            .getCollection<Character>("characters")
            .updateOne(
                Character::name eq characterName,
                pull(Character::proficiencies, proficiency),
            ).modifiedCount == 1L

    suspend fun subtractMoney(session: ClientSession, guildId: String, characterName: String, qty: Float) =
        client.getGuildDb(guildId).let {
            val character = getCharacter(session, guildId, characterName)
            it.getCollection<Character>("characters")
                .updateOne(
                    session,
                    Character::name eq characterName,
                    setValue(Character::money, character.money - qty),
                ).modifiedCount == 1L
        }

    suspend fun removeItemFromInventory(session: ClientSession, guildId: String, characterName: String, item: String, qty: Int) =
        client.getGuildDb(guildId).let {
            val c = getCharacter(session, guildId, characterName)
            val updatedCharacter = when {
                c.inventory[item] == null -> c
                c.inventory[item]!! <= qty -> c.copy(
                    inventory = c.inventory - item
                )
                c.inventory[item]!! > qty -> c.copy(
                    inventory = c.inventory + (item to c.inventory[item]!! - qty)
                )
                else -> c
            }
            it.getCollection<Character>("characters")
                .updateOne(
                    session,
                    Character::name eq characterName,
                    updatedCharacter
                ).modifiedCount == 1L
        }

    fun getCharactersWithPlayer(guildId: String, status: CharacterStatus? = null) =
        client.getGuildDb(guildId)
            .getCollection<Character>("characters")
            .aggregate<CharacterWithPlayer>(CharacterWithPlayer.getPipeline(status))
            .toFlow()

}
