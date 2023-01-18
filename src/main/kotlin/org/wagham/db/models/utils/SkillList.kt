package org.wagham.db.models.utils

import org.bson.codecs.pojo.annotations.BsonId

data class SkillList(
    @BsonId val utilType: String,
    val values: List<Purchasable> = emptyList()
)