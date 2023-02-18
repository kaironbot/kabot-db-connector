package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.AnnouncementBatch
import org.wagham.db.models.ExpTable
import org.wagham.db.models.ProficiencyList

class KabotDBUtilityScope(
    private val client: KabotMultiDBClient
) {

    suspend fun getExpTable(guildId: String) =
        client.getGuildDb(guildId)
           .getCollection<ExpTable>("utils")
           .findOne( ExpTable::utilType eq "msTable")
            ?: throw ResourceNotFoundException("ExpTable", "utils")

    suspend fun getProficiencies(guildId: String) =
        client.getGuildDb(guildId)
            .getCollection<ProficiencyList>("utils")
            .findOne(ProficiencyList::utilType eq "proficiencies")
            ?.values ?: emptyList()

    suspend fun getAnnouncements(guildId: String, batchId: String) =
        client.getGuildDb(guildId)
            .getCollection<AnnouncementBatch>("announcements")
            .findOne(AnnouncementBatch::id eq batchId)
            ?: throw ResourceNotFoundException("Announcements", "announcements")

    suspend fun updateAnnouncements(guildId: String, batchId: String, batch: AnnouncementBatch) =
        client.getGuildDb(guildId)
            .getCollection<AnnouncementBatch>("announcements")
            .updateOne(
                AnnouncementBatch::id eq batchId,
                batch,
                UpdateOptions().upsert(true)
            ).modifiedCount == 1L
}