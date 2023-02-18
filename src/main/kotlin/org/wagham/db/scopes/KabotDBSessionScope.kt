package org.wagham.db.scopes

import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Session
import org.wagham.db.pipelines.sessions.PlayerMasteredSessions

class KabotDBSessionScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Session> {

    override val collectionName = "sessions"

    override fun getMainCollection(guildId: String): CoroutineCollection<Session> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllSessions(guildId: String) =
        getMainCollection(guildId).find("{}").toFlow()

    fun getAllMasteredSessions(guildId: String, player: String) =
        getMainCollection(guildId)
            .aggregate<PlayerMasteredSessions>(PlayerMasteredSessions.getPipeline(player))
            .toFlow()
}