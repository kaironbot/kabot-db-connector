package org.wagham.db.scopes

import org.bson.Document
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.ExpTable


class KabotDBUtilityScope(
    private val client: KabotMultiDBClient
) {

    suspend fun getExpTable(guildId: String) =
        client.getGuildDb(guildId)?.getCollection<ExpTable>("utils")
            ?.findOne(Document(mapOf("_id" to "msTable")))
            ?: throw InvalidGuildException(guildId)

}