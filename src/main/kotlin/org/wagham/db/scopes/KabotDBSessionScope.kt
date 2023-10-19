package org.wagham.db.scopes

import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.models.Session
import org.wagham.db.pipelines.sessions.PlayerMasteredSessions
import org.wagham.db.pipelines.sessions.TimePassedInGame
import org.wagham.db.utils.daysInBetween
import java.util.Date

class KabotDBSessionScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Session> {

    override val collectionName = CollectionNames.SESSIONS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<Session> =
        client.getGuildDb(guildId).getCollection(collectionName)

    suspend fun getSessionByUid(guildId: String, sessionUid: Int) =
        getMainCollection(guildId).findOne(Session::uid eq sessionUid)

    fun getAllSessions(guildId: String, startDate: Date? = null, endDate: Date? = null) =
        getMainCollection(guildId).find(
            and(
                listOfNotNull(
                    startDate?.let { Session::date gte it },
                    endDate?.let { Session::date lte it }
                ).takeIf { it.isNotEmpty() } ?: listOf(Session::date ne null)
            )
        ).toFlow()

    fun getAllMasteredSessions(guildId: String, player: String) =
        getMainCollection(guildId)
            .aggregate<PlayerMasteredSessions>(PlayerMasteredSessions.getPipeline(player))
            .toFlow()

    suspend fun getTimePassedInGame(guildId: String, startDate: Date, endDate: Date) =
        getMainCollection(guildId)
            .aggregate<TimePassedInGame>(TimePassedInGame.getPipeline(startDate, endDate))
            .toList()
            .takeIf { it.isNotEmpty() }
            ?.fold(daysInBetween(startDate, endDate) + 1) { acc, it ->
                acc + it.days
            } ?: (daysInBetween(startDate, endDate) + 1)
}