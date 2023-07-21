package org.wagham.db.scopes

import com.mongodb.reactivestreams.client.ClientSession
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.push
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.enums.CollectionNames
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.Character
import org.wagham.db.models.Player
import java.util.*

class KabotDBPlayerScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Player> {

    override val collectionName = CollectionNames.PLAYERS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<Player> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllPlayers(guildId: String) =
        getMainCollection(guildId).find("{}").toFlow()

    suspend fun getPlayer(guildId: String, playerId: String) =
        getMainCollection(guildId).findOne(Player::playerId eq playerId)

    suspend fun getPlayer(session: ClientSession, guildId: String, playerId: String) =
        getMainCollection(guildId).findOne(session, Player::playerId eq playerId)

    suspend fun createPlayer(session: ClientSession, guildId: String, playerId: String, playerName: String): Player? {
        getMainCollection(guildId)
            .insertOne(
                session,
                Player(
                    playerId = playerId,
                    name = playerName,
                    dateJoined = Date()
                )
            )
        return getPlayer(session, guildId, playerId)
    }

    suspend fun connectPlayers(session: ClientSession, guildId: String, firstPlayerId: String, secondPlayerId: String) =
        getMainCollection(guildId).let {
            val firstUpdate = it.updateOne(
                session,
                Player::playerId eq firstPlayerId,
                push(Player::linkedPlayers, secondPlayerId)
            ).modifiedCount == 1L
            val secondUpdate = it.updateOne(
                session,
                Player::playerId eq secondPlayerId,
                push(Player::linkedPlayers, firstPlayerId)
            ).modifiedCount == 1L
            firstUpdate && secondUpdate
        }

    suspend fun setActiveCharacter(guildId: String, playerId: String, characterId: String): Boolean {
        val character = client.getGuildDb(guildId).getCollection<Character>(CollectionNames.CHARACTERS.stringValue)
            .findOne(
                Character::id eq characterId
            ) ?: throw ResourceNotFoundException(characterId, CollectionNames.CHARACTERS.stringValue)
        if(character.status != CharacterStatus.active)
            throw IllegalStateException("Character is not active")
        if(character.player != playerId)
            throw IllegalStateException("Character belongs to ${character.player} not to $playerId")
        return getMainCollection(guildId).updateOne(
            Player::playerId eq playerId,
            set(Player::activeCharacter setTo characterId)
        ).modifiedCount == 1L
    }

}
