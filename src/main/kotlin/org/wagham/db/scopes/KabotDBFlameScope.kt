package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.addToSet
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.Flame
import org.wagham.db.models.FlameCount
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class KabotDBFlameScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Flame> {

    override val collectionName = "flame"

    override fun getMainCollection(guildId: String): CoroutineCollection<Flame> =
        client.getGuildDb(guildId).getCollection(collectionName)

    suspend fun getFlame(guildId: String) =
        getMainCollection(guildId).findOne(
                Flame::id eq "flame"
            )?.flame ?: throw ResourceNotFoundException("flame", "flame")

    suspend fun addFlame(guildId: String, newFlame: String) =
        getMainCollection(guildId).findOneAndUpdate(
                Flame::id eq "flame",
                addToSet(Flame::flame, newFlame)
            ) ?: throw ResourceNotFoundException("flame", "flame")

    fun getFlameCount(guildId: String) =
        client.getGuildDb(guildId).getCollection<FlameCount>("flamecount").find("{}").toFlow()

    suspend fun addToFlameCount(guildId: String, count: Int = 1) =
        client.getGuildDb(guildId).let { db ->
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
        }

}
