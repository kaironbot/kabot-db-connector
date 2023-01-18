package org.wagham.db.scopes

import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.ExpTable
import org.wagham.db.models.utils.SkillList

class KabotDBUtilityScope(
    private val client: KabotMultiDBClient
) {

    suspend fun getExpTable(guildId: String) =
        client.getGuildDb(guildId)?.let {
            it.getCollection<ExpTable>("utils")
            .findOne( ExpTable::utilType eq "msTable") ?: throw ResourceNotFoundException("ExpTable")
        } ?: throw InvalidGuildException(guildId)

    suspend fun getProficiencies(guildId: String) =
        client.getGuildDb(guildId)?.let {
            it.getCollection<SkillList>("utils")
            .findOne(SkillList::utilType eq "proficiencies")
            ?.values ?: emptyList()
        } ?: throw InvalidGuildException(guildId)

}