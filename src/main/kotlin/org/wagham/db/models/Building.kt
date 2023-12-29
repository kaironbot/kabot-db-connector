package org.wagham.db.models

import kotlinx.serialization.Serializable

@Serializable
data class Building (
    val name: String,
    val description: String,
    val zone: String,
    val status: String
)