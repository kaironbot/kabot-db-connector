package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId

data class Race(
    @BsonId val id: String,
    val race: String,
    val subrace: String?,
    val link: String,
    val territories: List<String> = listOf()
)