package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Feat

class KabotDBFeatScope(
    private val client: KabotMultiDBClient
) {
    fun getAllFeats(guildId: String) =
        client.getGuildDb(guildId).getCollection<Feat>("feats").find("{}").toFlow()
}

