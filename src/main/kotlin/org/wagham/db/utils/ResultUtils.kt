package org.wagham.db.utils

import com.mongodb.client.result.UpdateResult
import org.litote.kmongo.coroutine.CoroutineFindPublisher
import kotlin.math.min

fun UpdateResult.isSuccessful() =
    (this.modifiedCount == 1L).takeIf {
        this.upsertedId == null
    } ?: true

fun <T> List<T>.subList(start: Int?, end: Int?): List<T> = subList(start ?: 0, min(end ?: size, size))

/**
 * @receiver a [CoroutineFindPublisher] of [T]
 * @param limit a nullable limit.
 * @return a [CoroutineFindPublisher] with the specified limit if [limit] is not null, or the same publisher otherwise.
 */
fun <T : Any> CoroutineFindPublisher<T>.limit(limit: Int?): CoroutineFindPublisher<T> =
    if(limit != null) limit(limit)
    else this