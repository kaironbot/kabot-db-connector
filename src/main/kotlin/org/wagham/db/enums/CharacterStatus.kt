package org.wagham.db.enums

import kotlinx.serialization.Serializable

@Serializable
enum class CharacterStatus {
    active,
    dead,
    npc,
    retired
}