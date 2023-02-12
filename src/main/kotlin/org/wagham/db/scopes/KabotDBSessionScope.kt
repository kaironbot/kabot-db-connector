package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Session

class KabotDBSessionScope(
    private val client: KabotMultiDBClient
) {
    fun getAllSessions(guildId: String) =
        client.getGuildDb(guildId).getCollection<Session>("sessions").find("{}").toFlow()
}