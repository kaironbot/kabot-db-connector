package org.wagham.db.enums

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.wagham.db.models.Building
import org.wagham.db.models.BuildingRecipe
import org.wagham.db.models.Character
import org.wagham.db.uuid

class BuildingRestrictionTypeTest: StringSpec ({

    "Should be able to check if a character reached the maximum number of buildings per type" {
        val limit = 3
        val building = BuildingRecipe(
            name = uuid(),
            type = uuid(),
            tier = uuid(),
            moCost = 100,
            bountyId = "",
            proficiencyReduction = "",
            size = ""
        )
        val okCharacter1 = Character(
            id = uuid(),
            name = uuid(),
            race = null,
            territory = null,
            characterClass = null,
            created = null,
            errata = emptyList(),
            errataMS = 0,
            lastMastered = null,
            lastPlayed = null,
            pbcMS = 0,
            masterMS = 0,
            player = uuid(),
            reputation = null,
            sessionMS = 0,
            status = CharacterStatus.active
        )
        val okCharacter2 = okCharacter1.copy(
            buildings = mapOf(
                "${building.name}:${building.type}:${building.tier}" to
                    List(limit-1) {(Building(uuid(), uuid(), uuid(), uuid()))}
            )
        )
        val noOkCharacter = okCharacter1.copy(
            buildings = mapOf(
                "${building.name}:${building.type}:${building.tier}" to
                    List(limit) {(Building(uuid(), uuid(), uuid(), uuid()))}
            )
        )
        BuildingRestrictionType.TYPE_RESTRICTION.validator(limit, okCharacter1, building) shouldBe true
        BuildingRestrictionType.TYPE_RESTRICTION.validator(limit, okCharacter2, building) shouldBe true
        BuildingRestrictionType.TYPE_RESTRICTION.validator(limit, noOkCharacter, building) shouldBe false
    }

})