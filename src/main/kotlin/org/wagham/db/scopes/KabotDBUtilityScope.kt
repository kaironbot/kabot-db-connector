package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import kotlinx.coroutines.flow.Flow
import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.*
import org.wagham.db.utils.getCollection
import org.wagham.db.utils.isSuccessful
import org.wagham.db.utils.limit

class KabotDBUtilityScope(
    private val client: KabotMultiDBClient
) {

    suspend fun getExpTable(guildId: String) =
        client.getGuildDb(guildId)
           .getCollection<ExpTable>(CollectionNames.UTILS)
           .findOne( ExpTable::utilType eq "msTable")
            ?: throw ResourceNotFoundException("ExpTable", "utils")

    suspend fun getPlayableResources(guildId: String) =
        client.getGuildDb(guildId)
            .getCollection<PlayableResources>(CollectionNames.UTILS)
            .findOne( PlayableResources::docId eq "playableResources")
            ?: throw ResourceNotFoundException("Playable Resources", "utils")

    suspend fun getAnnouncements(guildId: String, batchId: String) =
        client.getGuildDb(guildId)
            .getCollection<AnnouncementBatch>(CollectionNames.ANNOUNCEMENTS)
            .findOne(AnnouncementBatch::id eq batchId)
            ?: throw ResourceNotFoundException("Announcements", "announcements")

    suspend fun updateAnnouncements(guildId: String, batchId: String, batch: AnnouncementBatch) =
        client.getGuildDb(guildId)
            .getCollection<AnnouncementBatch>(CollectionNames.ANNOUNCEMENTS)
            .updateOne(
                AnnouncementBatch::id eq batchId,
                batch,
                UpdateOptions().upsert(true)
            ).isSuccessful()

    fun getBuildingsMessages(guildId: String) =
        client.getGuildDb(guildId)
            .getCollection<PlayerBuildingsMessages>(CollectionNames.BUILDING_MESSAGES)
            .find()
            .toFlow()

    suspend fun updateBuildingMessage(guildId: String, message: PlayerBuildingsMessages) =
        client.getGuildDb(guildId)
            .getCollection<PlayerBuildingsMessages>(CollectionNames.BUILDING_MESSAGES)
            .updateOne(
                PlayerBuildingsMessages::id eq message.id,
                message,
                UpdateOptions().upsert(true)
            ).isSuccessful()

    suspend fun updateAttendance(guildId: String, attendanceReport: AttendanceReport) =
        client.getGuildDb(guildId)
            .getCollection<AttendanceReport>(CollectionNames.ATTENDANCE)
            .updateOne(
                AttendanceReport::date eq attendanceReport.date,
                attendanceReport,
                UpdateOptions().upsert(true)
            ).isSuccessful()

    suspend fun getLastAttendance(guildId: String) =
        client.getGuildDb(guildId)
            .getCollection<AttendanceReport>(CollectionNames.ATTENDANCE)
            .find()
            .descendingSort(AttendanceReport::date)
            .first()

    suspend fun getLastMarket(guildId: String): WeeklyMarket? =
        client.getGuildDb(guildId)
            .getCollection<WeeklyMarket>(CollectionNames.MARKETS)
            .find()
            .descendingSort(WeeklyMarket::date)
            .first()

    /**
     * @param guildId the id of the guild.
     * @param limit the number of elements to retrieve. Null will retrieve all the elements.
     * @return a [Flow] of [WeeklyMarket] sorted descending by [WeeklyMarket.date].
     */
    fun getLastMarkets(guildId: String, limit: Int? = null): Flow<WeeklyMarket> =
        client.getGuildDb(guildId)
            .getCollection<WeeklyMarket>(CollectionNames.MARKETS)
            .find()
            .limit(limit)
            .descendingSort(WeeklyMarket::date)
            .toFlow()

    suspend fun updateMarket(guildId: String, market: WeeklyMarket) =
        client.getGuildDb(guildId)
            .getCollection<WeeklyMarket>(CollectionNames.MARKETS)
            .updateOne(
                WeeklyMarket::date eq market.date,
                market,
                UpdateOptions().upsert(true)
            ).isSuccessful()
}