package org.wagham.db.models.embed

import kotlinx.serialization.Serializable

@Serializable
data class LabelStub(
    val id: String,
    val name: String
)