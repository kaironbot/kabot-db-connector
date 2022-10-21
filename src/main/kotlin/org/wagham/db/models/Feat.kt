package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId

data class Feat (
    @BsonId val name: String,
    val race: List<String>?,
    val asi: Boolean,
    val link: String,
    val source: String
)