package org.wagham.db.utils

import com.mongodb.client.result.UpdateResult

fun UpdateResult.isSuccessful() =
    (this.modifiedCount == 1L).takeIf {
        this.upsertedId == null
    } ?: true