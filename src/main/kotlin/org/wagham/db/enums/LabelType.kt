package org.wagham.db.enums

import kotlinx.serialization.Serializable

@Serializable
enum class LabelType {
    CHARACTER,
    SESSION,
    ITEM,
    PROFICIENCY
}