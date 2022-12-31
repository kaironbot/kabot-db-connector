package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import java.util.Date

data class Flame(
    @BsonId val id: String,
    val flame: Set<String> = emptySet()
)

data class FlameCount(
    @BsonId val date: Date,
    val count: Int
)