package org.wagham.db.scopes

import com.mongodb.client.model.Updates
import com.mongodb.reactivestreams.client.ClientSession
import org.bson.BsonDocument
import org.bson.Document
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.enums.CollectionNames
import org.wagham.db.exceptions.NoActiveCharacterException
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.Building
import org.wagham.db.models.BuildingRecipe
import org.wagham.db.models.Character
import org.wagham.db.models.embed.ProficiencyStub
import org.wagham.db.pipelines.characters.CharacterWithPlayer


class KabotDBCharacterScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Character> {

    override val collectionName = CollectionNames.CHARACTERS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<Character> =
        client.getGuildDb(guildId).getCollection(collectionName)

    suspend fun getActiveCharacter(guildId: String, playerId: String): Character =
        getMainCollection(guildId)
            .findOne(Character::status eq CharacterStatus.active, Character::player eq playerId)
            ?: throw NoActiveCharacterException(playerId)

    suspend fun getCharacter(guildId: String, characterId: String): Character =
        getMainCollection(guildId)
            .findOne(Character::id eq characterId)
            ?: throw ResourceNotFoundException(characterId, "characters")

    suspend fun getCharacter(session: ClientSession, guildId: String, characterId: String): Character =
        getMainCollection(guildId)
            .findOne(session, Character::id eq characterId)
            ?: throw ResourceNotFoundException(characterId, "characters")

    suspend fun updateCharacter(guildId: String, updatedCharacter: Character): Boolean =
        getMainCollection(guildId)
            .updateOne(
                Character::id eq updatedCharacter.id,
                updatedCharacter
            ).modifiedCount == 1L

    fun getAllCharacters(guildId: String, status: CharacterStatus? = null) =
        getMainCollection(guildId)
            .find(Document(
                status?.let {
                    mapOf("status" to status)
                } ?: emptyMap<String, String>()
            ))
            .toFlow()

    suspend fun addProficiencyToCharacter(guildId: String, characterId: String, proficiency: ProficiencyStub) =
        getMainCollection(guildId)
            .updateOne(
                Character::id eq characterId,
                addToSet(Character::proficiencies, proficiency)
            ).modifiedCount == 1L

    suspend fun addProficiencyToCharacter(session: ClientSession, guildId: String, characterId: String, proficiency: ProficiencyStub) =
        getMainCollection(guildId)
            .updateOne(
                session,
                Character::id eq characterId,
                addToSet(Character::proficiencies, proficiency)
            ).modifiedCount == 1L

    suspend fun addLanguageToCharacter(session: ClientSession, guildId: String, characterId: String, language: ProficiencyStub) =
        getMainCollection(guildId)
            .updateOne(
                session,
                Character::id eq characterId,
                addToSet(Character::languages, language)
            ).modifiedCount == 1L

    suspend fun removeProficiencyFromCharacter(guildId: String, characterId: String, proficiency: ProficiencyStub) =
        getMainCollection(guildId)
            .updateOne(
                Character::id eq characterId,
                pull(Character::proficiencies, proficiency),
            ).modifiedCount == 1L

    suspend fun removeLanguageFromCharacter(session: ClientSession, guildId: String, characterId: String, language: ProficiencyStub) =
        getMainCollection(guildId)
            .updateOne(
                session,
                Character::id eq characterId,
                pull(Character::languages, language),
            ).modifiedCount == 1L

    suspend fun subtractMoney(session: ClientSession, guildId: String, characterId: String, qty: Float) =
        client.getGuildDb(guildId).let {
            val character = getCharacter(session, guildId, characterId)
            it.getCollection<Character>(collectionName)
                .updateOne(
                    session,
                    Character::id eq characterId,
                    setValue(Character::money, character.money - qty),
                ).modifiedCount == 1L
        }

    suspend fun addMoney(session: ClientSession, guildId: String, characterId: String, qty: Float) =
        client.getGuildDb(guildId).let {
            val character = getCharacter(session, guildId, characterId)
            it.getCollection<Character>(collectionName)
                .updateOne(
                    session,
                    Character::id eq characterId,
                    setValue(Character::money, character.money + qty),
                ).modifiedCount == 1L
        }

    suspend fun removeItemFromInventory(session: ClientSession, guildId: String, characterId: String, item: String, qty: Int) =
        client.getGuildDb(guildId).let {
            val c = getCharacter(session, guildId, characterId)
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
                    Character::id eq characterId,
                    updatedCharacter
                ).modifiedCount == 1L
        }

    suspend fun removeItemFromAllInventories(session: ClientSession, guildId: String, item: String) =
        getMainCollection(guildId).updateMany(
            session,
            BsonDocument(),
            Updates.unset("inventory.$item")
        ).let { true }

    suspend fun addItemToInventory(session: ClientSession, guildId: String, characterId: String, item: String, qty: Int) =
        client.getGuildDb(guildId).let {
            val c = getCharacter(session, guildId, characterId)
            it.getCollection<Character>(collectionName)
                .updateOne(
                    session,
                    Character::id eq characterId,
                    c.copy(
                        inventory = c.inventory + (item to (c.inventory[item] ?: 0) + qty)
                    )
                ).modifiedCount == 1L
        }

    suspend fun addBuilding(session: ClientSession, guildId: String, characterId: String, building: Building, type: BuildingRecipe) =
        client.getGuildDb(guildId).let {
            val c = getCharacter(session, guildId, characterId)
            val bId = "${type.name}:${type.type}:${type.tier}"
            it.getCollection<Character>(collectionName)
                .updateOne(
                    session,
                    Character::id eq characterId,
                    c.copy(
                        buildings = c.buildings +
                            (bId to (c.buildings[bId] ?: emptyList()) + building)
                    )
                ).modifiedCount == 1L
        }

    suspend fun removeBuilding(session: ClientSession, guildId: String, characterId: String, buildingId: String, type: BuildingRecipe) =
        client.getGuildDb(guildId).let {
            val c = getCharacter(session, guildId, characterId)
            val bId = "${type.name}:${type.type}:${type.tier}"
            it.getCollection<Character>(collectionName)
                .updateOne(
                    session,
                    Character::id eq characterId,
                    c.copy(
                        buildings = c.buildings +
                            (bId to (c.buildings[bId] ?: emptyList()).filter { b -> b.name != buildingId })
                    )
                ).modifiedCount == 1L
        }

    fun getCharactersWithPlayer(guildId: String, status: CharacterStatus? = null) =
        getMainCollection(guildId)
            .aggregate<CharacterWithPlayer>(CharacterWithPlayer.getPipeline(status))
            .toFlow()

}
