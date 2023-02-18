package org.wagham.db.scopes

import org.litote.kmongo.coroutine.CoroutineCollection
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

}
