package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Background

class KabotDBBackgroundScope(
    private val client: KabotMultiDBClient
) {
    fun getAllBackgrounds(guildId: String) =
        client.getGuildDb(guildId).getCollection<Background>("backgrounds").find("{}").toFlow()
}

