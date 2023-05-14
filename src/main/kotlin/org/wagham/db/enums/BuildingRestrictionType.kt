package org.wagham.db.enums

import org.wagham.db.models.BaseBuilding
import org.wagham.db.models.Character

enum class BuildingRestrictionType(val validator: (Int, Character, BaseBuilding) -> Boolean) {
    TYPE_RESTRICTION({ limit, character, building ->
        character.buildings.entries.associate { (compositeId, buildings) ->
            compositeId.split(":")[1] to buildings.size
        }[building.type]?.let {
            it < limit
        } ?: true
    })
}