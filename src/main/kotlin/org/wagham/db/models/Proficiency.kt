package org.wagham.db.models

import org.wagham.db.models.embed.AbilityCost
import org.wagham.db.models.embed.LabelStub

interface Proficiency {
    val id: String
    val name: String
    val cost: AbilityCost?
    val labels: Set<LabelStub>
}