package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.models.embed.AbilityCost

data class LanguageProficiency(
    @BsonId override val name: String,
    override val cost: AbilityCost? = null
) : Proficiency