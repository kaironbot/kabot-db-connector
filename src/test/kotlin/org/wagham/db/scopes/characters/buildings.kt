package org.wagham.db.scopes.characters

import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.models.Building
import org.wagham.db.uuid

fun KabotMultiDBClientTest.testCharactersBuildings(
    client: KabotMultiDBClient,
    guildId: String
) {

    "addBuilding can add a building of a new type to a character" {
        val buildingType = client.buildingsScope.getAllBuildingRecipes(guildId).toList().random()
        val bId = "${buildingType.name}:${buildingType.type}:${buildingType.tier}"
        val character = client.charactersScope.getAllCharacters(guildId).take(1000).toList().random()
        val newBuilding = Building(uuid(), uuid(), uuid(), uuid())
        val result = client.transaction(guildId) {
            client.charactersScope.addBuilding(
                it,
                guildId,
                character.id,
                newBuilding,
                buildingType
            )
            true
        }
        result.committed shouldBe true
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
        updatedCharacter.buildings[bId] shouldNotBe null
        updatedCharacter.buildings[bId]!!.size shouldBe 1
        updatedCharacter.buildings[bId]!!.first() shouldBe newBuilding
    }

    "addBuilding can add a building of an existing type to a character" {
        val buildingType = client.buildingsScope.getAllBuildingRecipes(guildId).toList().random()
        val bId = "${buildingType.name}:${buildingType.type}:${buildingType.tier}"
        val character = client.charactersScope.getAllCharacters(guildId).take(1000).toList().random()

        val oldBuilding = Building(uuid(), uuid(), uuid(), uuid())
        client.transaction(guildId) {
            client.charactersScope.addBuilding(
                it,
                guildId,
                character.id,
                oldBuilding,
                buildingType
            )
        }.committed shouldBe true
        val newBuilding = Building(uuid(), uuid(), uuid(), uuid())
        val result = client.transaction(guildId) {
            client.charactersScope.addBuilding(
                it,
                guildId,
                character.id,
                newBuilding,
                buildingType
            )
        }
        result.committed shouldBe true
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
        updatedCharacter.buildings[bId] shouldNotBe null
        updatedCharacter.buildings[bId]!!.size shouldBeGreaterThan 1
        updatedCharacter.buildings[bId]!!.find {
            it.name == newBuilding.name
        } shouldBe newBuilding
    }

    "Can remove a building from a player" {
        val buildingType = client.buildingsScope.getAllBuildingRecipes(guildId).toList().random()
        val bId = "${buildingType.name}:${buildingType.type}:${buildingType.tier}"
        val character = client.charactersScope.getAllCharacters(guildId).take(1000).toList().random()

        val oldBuilding = Building(uuid(), uuid(), uuid(), uuid())
        client.transaction(guildId) {
            client.charactersScope.addBuilding(
                it,
                guildId,
                character.id,
                oldBuilding,
                buildingType
            )
        }.committed shouldBe true

        val result = client.transaction(guildId) {
            client.charactersScope.removeBuilding(
                it,
                guildId,
                character.id,
                oldBuilding.name,
                buildingType
            )
        }
        result.committed shouldBe true
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
        updatedCharacter.buildings[bId]?.firstOrNull { it.name == oldBuilding.name } shouldBe null
    }
}