package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.models.embed.Prize

data class Bounty (
    @BsonId val id: String,
    val prizes: List<Prize>
)
