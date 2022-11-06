package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.Bounty

class KabotDBBountyScope(
    private val client: KabotMultiDBClient
) {
    fun getAllBounties(guildId: String) =
        client.getGuildDb(guildId)?.getCollection<Bounty>("bounties")?.find("{}")?.toFlow()
            ?: throw InvalidGuildException(guildId)
}
