package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.models.embed.AbilityCost
import org.wagham.db.models.embed.LabelStub

data class ToolProficiency(
    @BsonId override val id: String,
    override val name: String,
    override val cost: AbilityCost? = null,
    override val labels: Set<LabelStub> = emptySet()
) : Proficiency
