package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Player

class KabotDBPlayerScope(
    private val client: KabotMultiDBClient
) {
    fun getAllPlayers(guildId: String) =
        client.getGuildDb(guildId).getCollection<Player>("players").find("{}").toFlow()

}
