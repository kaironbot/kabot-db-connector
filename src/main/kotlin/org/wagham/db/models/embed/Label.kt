package org.wagham.db.models.embed

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.enums.LabelType

@Serializable
data class Label(
    @BsonId val id: String,
    val name: String,
    val types: Set<LabelType> = emptySet(),
    val description: String? = null
) {
    fun toLabelStub(): LabelStub = LabelStub(id, name)
}