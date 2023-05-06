package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.AnnouncementBatch
import org.wagham.db.models.ExpTable
import org.wagham.db.models.PlayerBuildingsMessages
import org.wagham.db.utils.isSuccessful

class KabotDBUtilityScope(
    private val client: KabotMultiDBClient
) {

    suspend fun getExpTable(guildId: String) =
        client.getGuildDb(guildId)
           .getCollection<ExpTable>(CollectionNames.UTILS.stringValue)
           .findOne( ExpTable::utilType eq "msTable")
            ?: throw ResourceNotFoundException("ExpTable", "utils")

    suspend fun getAnnouncements(guildId: String, batchId: String) =
        client.getGuildDb(guildId)
            .getCollection<AnnouncementBatch>(CollectionNames.ANNOUNCEMENTS.stringValue)
            .findOne(AnnouncementBatch::id eq batchId)
            ?: throw ResourceNotFoundException("Announcements", "announcements")

    suspend fun updateAnnouncements(guildId: String, batchId: String, batch: AnnouncementBatch) =
        client.getGuildDb(guildId)
            .getCollection<AnnouncementBatch>(CollectionNames.ANNOUNCEMENTS.stringValue)
            .updateOne(
                AnnouncementBatch::id eq batchId,
                batch,
                UpdateOptions().upsert(true)
            ).isSuccessful()

    fun getBuildingsMessages(guildId: String) =
        client.getGuildDb(guildId)
            .getCollection<PlayerBuildingsMessages>(CollectionNames.BUILDING_MESSAGES.stringValue)
            .find()
            .toFlow()

    suspend fun updateBuildingMessage(guildId: String, message: PlayerBuildingsMessages) =
        client.getGuildDb(guildId)
            .getCollection<PlayerBuildingsMessages>(CollectionNames.BUILDING_MESSAGES.stringValue)
            .updateOne(
                PlayerBuildingsMessages::id eq message.id,
                message,
                UpdateOptions().upsert(true)
            ).isSuccessful()
}