package org.wagham.db.models.embed

import kotlinx.serialization.Serializable

@Serializable
data class ProficiencyStub(
    val id: String,
    val name: String
)