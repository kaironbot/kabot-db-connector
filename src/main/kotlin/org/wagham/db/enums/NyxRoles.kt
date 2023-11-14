package org.wagham.db.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NyxRoles {
    @SerialName("a") ADMIN,
    @SerialName("mS") MANAGE_SESSIONS
}