package org.wagham.db.models.embed

data class EventConfig(
    val enabled: Boolean = false,
    val allowedChannels: Set<String> = emptySet()
)
