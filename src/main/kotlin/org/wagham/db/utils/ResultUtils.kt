package org.wagham.db.utils

import com.mongodb.client.result.UpdateResult
import kotlin.math.min

fun UpdateResult.isSuccessful() =
    (this.modifiedCount == 1L).takeIf {
        this.upsertedId == null
    } ?: true

fun <T> List<T>.subList(start: Int?, end: Int?): List<T> = subList(start ?: 0, min(end ?: size, size))