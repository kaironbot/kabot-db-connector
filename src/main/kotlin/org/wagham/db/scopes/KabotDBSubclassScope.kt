package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Subclass

class KabotDBSubclassScope(
    private val client: KabotMultiDBClient
) {
    fun getAllSubclasses(guildId: String) =
        client.getGuildDb(guildId).getCollection<Subclass>("classes").find("{}").toFlow()

}

