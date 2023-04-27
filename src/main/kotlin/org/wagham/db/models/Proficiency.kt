package org.wagham.db.models

import org.wagham.db.models.embed.AbilityCost

interface Proficiency {
    val name: String
    val cost: AbilityCost?
}