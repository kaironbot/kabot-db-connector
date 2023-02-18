package org.wagham.db.scopes

import com.mongodb.reactivestreams.client.ClientSession
import org.bson.Document
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.exceptions.NoActiveCharacterException
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.Character
import org.wagham.db.pipelines.characters.CharacterWithPlayer


class KabotDBCharacterScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Character> {

    override val collectionName = "characters"

    override fun getMainCollection(guildId: String): CoroutineCollection<Character> =
        client.getGuildDb(guildId).getCollection(collectionName)

    suspend fun getActiveCharacter(guildId: String, playerId: String): Character =
        getMainCollection(guildId)
            .findOne(Character::status eq CharacterStatus.active, Character::player eq playerId)
            ?: throw NoActiveCharacterException(playerId)

    suspend fun getCharacter(guildId: String, characterName: String): Character =
        getMainCollection(guildId)
            .findOne(Character::name eq characterName)
            ?: throw ResourceNotFoundException(characterName, "characters")

    suspend fun getCharacter(session: ClientSession, guildId: String, characterName: String): Character =
        getMainCollection(guildId)
            .findOne(session, Character::name eq characterName)
            ?: throw ResourceNotFoundException(characterName, "characters")


    fun getAllCharacters(guildId: String, status: CharacterStatus? = null) =
        getMainCollection(guildId)
            .find(Document(
                status?.let {
                    mapOf("status" to status)
                } ?: emptyMap<String, String>()
            ))
            .toFlow()

    suspend fun addProficiencyToCharacter(guildId: String, characterName: String, proficiency: String) =
        getMainCollection(guildId)
            .updateOne(
                Character::name eq characterName,
                addToSet(Character::proficiencies, proficiency)
            ).modifiedCount == 1L

    suspend fun addProficiencyToCharacter(session: ClientSession, guildId: String, characterName: String, proficiency: String) =
        getMainCollection(guildId)
            .updateOne(
                session,
                Character::name eq characterName,
                addToSet(Character::proficiencies, proficiency)
            ).modifiedCount == 1L

    suspend fun removeProficiencyFromCharacter(guildId: String, characterName: String, proficiency: String) =
        getMainCollection(guildId)
            .updateOne(
                Character::name eq characterName,
                pull(Character::proficiencies, proficiency),
            ).modifiedCount == 1L

    suspend fun subtractMoney(session: ClientSession, guildId: String, characterName: String, qty: Float) =
        client.getGuildDb(guildId).let {
            val character = getCharacter(session, guildId, characterName)
            it.getCollection<Character>(collectionName)
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
            it.getCollection<Character>(collectionName)
                .updateOne(
                    session,
                    Character::name eq characterName,
                    updatedCharacter
                ).modifiedCount == 1L
        }

    fun getCharactersWithPlayer(guildId: String, status: CharacterStatus? = null) =
        getMainCollection(guildId)
            .aggregate<CharacterWithPlayer>(CharacterWithPlayer.getPipeline(status))
            .toFlow()

}
