package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Race

class KabotDBRaceScope(
    private val client: KabotMultiDBClient
) {
    fun getAllRaces(guildId: String) =
        client.getGuildDb(guildId).getCollection<Race>("races").find("{}").toFlow()
}