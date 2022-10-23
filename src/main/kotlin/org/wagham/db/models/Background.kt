package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId

data class Background(
    @BsonId val name: String,
    val race: String,
    val link: String
)