package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Spell

class KabotDBSpellScope(
    private val client: KabotMultiDBClient
) {
    fun getAllSpells(guildId: String) =
        client.getGuildDb(guildId).getCollection<Spell>("spells").find("{}").toFlow()
}