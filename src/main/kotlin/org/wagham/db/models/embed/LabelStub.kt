package org.wagham.db.models.embed

import org.bson.codecs.pojo.annotations.BsonId

data class LabelStub(
    @BsonId val id: String,
    val name: String
)