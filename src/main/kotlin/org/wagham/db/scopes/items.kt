package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.Item

class KabotDBItemScope(
    private val client: KabotMultiDBClient
) {
    fun getAllItems(guildId: String) =
        client.getGuildDb(guildId)?.getCollection<Item>("items")?.find("{}")?.toFlow() ?: throw InvalidGuildException(guildId)

}

