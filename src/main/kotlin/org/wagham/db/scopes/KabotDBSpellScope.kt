package org.wagham.db.scopes

import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Spell

class KabotDBSpellScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Spell> {

    override val collectionName = "spells"

    override fun getMainCollection(guildId: String): CoroutineCollection<Spell> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllSpells(guildId: String) =
        getMainCollection(guildId).find("{}").toFlow()

    suspend fun rewriteAllSpells(guildId: String, spells: List<Spell>) =
        getMainCollection(guildId)
            .let {
                it.deleteMany("{}")
                it.insertMany(spells)
            }.insertedIds.size == spells.size
}