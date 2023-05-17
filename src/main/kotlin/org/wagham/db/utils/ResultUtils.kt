package org.wagham.db.utils

import com.mongodb.client.result.UpdateResult

fun UpdateResult.isSuccessful() =
    (this.modifiedCount == 1L).takeIf {
        this.upsertedId == null
    } ?: true

fun <T> List<T>.subList(start: Int?, end: Int?): List<T> = subList(start ?: 0, end ?: size)