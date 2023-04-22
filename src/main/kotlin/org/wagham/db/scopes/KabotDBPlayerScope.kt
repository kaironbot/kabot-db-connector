package org.wagham.db.scopes

import com.mongodb.session.ClientSession
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.push
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Player

class KabotDBPlayerScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Player> {

    override val collectionName = "players"

    override fun getMainCollection(guildId: String): CoroutineCollection<Player> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllPlayers(guildId: String) =
        getMainCollection(guildId).find("{}").toFlow()

    suspend fun getPlayer(guildId: String, playerId: String) =
        getMainCollection(guildId)
            .findOne(Player::playerId eq playerId)

    suspend fun connectPlayers(session: ClientSession, guildId: String, firstPlayerId: String, secondPlayerId: String) =
        getMainCollection(guildId).let {
            val firstUpdate = it.updateOne(
                Player::playerId eq firstPlayerId,
                push(Player::linkedPlayers, secondPlayerId)
            ).modifiedCount == 1L
            val secondUpdate = it.updateOne(
                Player::playerId eq secondPlayerId,
                push(Player::linkedPlayers, firstPlayerId)
            ).modifiedCount == 1L
            firstUpdate && secondUpdate
        }

}
