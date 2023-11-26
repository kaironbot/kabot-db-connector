package org.wagham.db.models.embed

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class LabelStub(
    @BsonId val id: String,
    val name: String
)