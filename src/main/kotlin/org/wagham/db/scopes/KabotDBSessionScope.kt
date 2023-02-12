package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Session
import org.wagham.db.pipelines.sessions.PlayerMasteredSessions

class KabotDBSessionScope(
    private val client: KabotMultiDBClient
) {
    fun getAllSessions(guildId: String) =
        client.getGuildDb(guildId).getCollection<Session>("sessions").find("{}").toFlow()

    fun getAllMasteredSessions(guildId: String, player: String) =
        client.getGuildDb(guildId)
            .getCollection<Session>("sessions")
            .aggregate<PlayerMasteredSessions>(PlayerMasteredSessions.getPipeline(player))
            .toFlow()
}