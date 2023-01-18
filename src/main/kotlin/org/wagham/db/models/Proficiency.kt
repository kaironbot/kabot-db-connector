package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId

data class Proficiency(
    val name: String,
    val isPurchasable: Boolean
)

data class ProficiencyList(
    @BsonId val utilType: String,
    val values: List<Proficiency> = emptyList()
)