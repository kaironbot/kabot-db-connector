package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import org.bson.BsonDocument
import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.*
import org.wagham.db.utils.dateAtMidnight
import org.wagham.db.utils.isSuccessful
import java.util.*

class KabotDBUtilityScope(
    private val client: KabotMultiDBClient
) {

    suspend fun getExpTable(guildId: String) =
        client.getGuildDb(guildId)
           .getCollection<ExpTable>(CollectionNames.UTILS.stringValue)
           .findOne( ExpTable::utilType eq "msTable")
            ?: throw ResourceNotFoundException("ExpTable", "utils")

    suspend fun getPlayableResources(guildId: String) =
        client.getGuildDb(guildId)
            .getCollection<PlayableResources>(CollectionNames.UTILS.stringValue)
            .findOne( PlayableResources::docId eq "playableResources")
            ?: throw ResourceNotFoundException("Playable Resources", "utils")

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

    suspend fun updateAttendance(guildId: String, attendanceReport: AttendanceReport) =
        client.getGuildDb(guildId)
            .getCollection<AttendanceReport>(CollectionNames.ATTENDANCE.stringValue)
            .updateOne(
                AttendanceReport::date eq attendanceReport.date,
                attendanceReport,
                UpdateOptions().upsert(true)
            ).isSuccessful()

    suspend fun getLastAttendance(guildId: String) =
        client.getGuildDb(guildId)
            .getCollection<AttendanceReport>(CollectionNames.ATTENDANCE.stringValue)
            .find()
            .descendingSort(AttendanceReport::date)
            .first() ?: throw ResourceNotFoundException("Today's attendance", CollectionNames.ATTENDANCE.stringValue)
}