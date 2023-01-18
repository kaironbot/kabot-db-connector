package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.addToSet
import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.Flame
import org.wagham.db.models.FlameCount
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class KabotDBFlameScope(
    private val client: KabotMultiDBClient
) {
    suspend fun getFlame(guildId: String) =
        client.getGuildDb(guildId)?.let { db ->
            db.getCollection<Flame>("flame").findOne(
                Flame::id eq "flame"
            )?.flame ?: throw ResourceNotFoundException("flame", "flame")
        } ?: throw InvalidGuildException(guildId)

    suspend fun addFlame(guildId: String, newFlame: String) =
        client.getGuildDb(guildId)?.let { db ->
            db.getCollection<Flame>("flame").findOneAndUpdate(
                Flame::id eq "flame",
                addToSet(Flame::flame, newFlame)
            ) ?: throw ResourceNotFoundException("flame", "flame")
        } ?: throw InvalidGuildException(guildId)

    fun getFlameCount(guildId: String) =
        client.getGuildDb(guildId)?.getCollection<FlameCount>("flamecount")?.find("{}")?.toFlow() ?: throw InvalidGuildException(guildId)

    suspend fun addToFlameCount(guildId: String, count: Int = 1) =
        client.getGuildDb(guildId)?.let { db ->
            val calendar = Calendar.getInstance()
            val startingDate = LocalDateTime.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            val dateId = Date.from(startingDate.toInstant(ZoneOffset.UTC))
            val currentCount = db.getCollection<FlameCount>("flamecount").findOne(
                FlameCount::date eq dateId
            ) ?: FlameCount(dateId, 0)
            db.getCollection<FlameCount>("flamecount").updateOne(
                FlameCount::date eq dateId,
                currentCount.copy( count = currentCount.count+count),
                UpdateOptions().upsert(true)
            )
        } ?: throw InvalidGuildException(guildId)

}
