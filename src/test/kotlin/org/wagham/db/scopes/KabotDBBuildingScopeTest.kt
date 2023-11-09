package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.MongoCredentials
import org.wagham.db.uuid

class KabotDBBuildingScopeTest : StringSpec() {

    private val client = KabotMultiDBClient(
        MongoCredentials(
            "ADMIN",
            System.getenv("DB_TEST_USER").shouldNotBeNull(),
            System.getenv("DB_TEST_PWD").shouldNotBeNull(),
            System.getenv("TEST_DB").shouldNotBeNull(),
            System.getenv("DB_TEST_IP").shouldNotBeNull(),
            System.getenv("DB_TEST_PORT").shouldNotBeNull().toInt(),
        )
    )
    private val guildId = System.getenv("TEST_DB_ID").shouldNotBeNull()

    init {
        testBuildings()
    }

    private fun StringSpec.testBuildings() {

        "getAllBuildingRecipes should be able to get all the buildings" {
            client.buildingsScope.getAllBuildingRecipes(guildId).count() shouldBeGreaterThan 0
        }

        "getAllBuildingRecipes should noy be able to get all the buildings for a non-existent guild" {
            shouldThrow<InvalidGuildException> {
                client.buildingsScope.getAllBuildingRecipes(uuid())
            }
        }

        "getBuildingsWithBounty should be able to get all the buildings" {
            client.buildingsScope.getBuildingsWithBounty(guildId).count() shouldBeGreaterThan 0
        }

        "getBuildingsWithBounty should noy be able to get all the buildings for a non-existent guild" {
            shouldThrow<InvalidGuildException> {
                client.buildingsScope.getBuildingsWithBounty(uuid())
            }
        }

        "Should be able of updating a list of buildings" {
            val buildingsToUpdate = client.buildingsScope.getAllBuildingRecipes(guildId)
                .take(3)
                .map { it.copy(proficiencyReduction = uuid(), bountyId = uuid()) }
                .toList()

            client.buildingsScope.updateBuildings(guildId, buildingsToUpdate) shouldBe true

            client.buildingsScope.getAllBuildingRecipes(guildId)
                .filter { b -> buildingsToUpdate.map { it.name }.contains(b.name) }
                .onEach { b ->
                    val oldBuilding = buildingsToUpdate.first{ it.name == b.name }
                    b.proficiencyReduction shouldBe oldBuilding.proficiencyReduction
                    b.bountyId shouldBe oldBuilding.bountyId
                }.collect()
        }

        "If a building does not exist, it is inserted" {
            val buildingToUpdate = client.buildingsScope.getAllBuildingRecipes(guildId)
                .first()
                .copy(
                    name = uuid(),
                    proficiencyReduction = uuid(),
                    bountyId = uuid()
                )

            client.buildingsScope.updateBuilding(guildId, buildingToUpdate) shouldBe true

            client.buildingsScope.getAllBuildingRecipes(guildId)
                .first { it.name == buildingToUpdate.name }
                .let {
                    it.proficiencyReduction shouldBe buildingToUpdate.proficiencyReduction
                    it.bountyId shouldBe buildingToUpdate.bountyId
                }
        }

        "Cannot update buildings from a non existent guild" {
            val buildingsToUpdate = client.buildingsScope.getAllBuildingRecipes(guildId).toList()
            shouldThrow<InvalidGuildException> {
                client.buildingsScope.updateBuildings(uuid(), buildingsToUpdate)
            }
        }

        "Can delete buildings from a guild" {
            val newBuildings = client.buildingsScope.getAllBuildingRecipes(guildId)
                .take(2)
                .map { it.copy(name = uuid(), proficiencyReduction = uuid(), bountyId = uuid()) }
                .toList()

            val newBuildingsId = newBuildings.map { it.name }

            client.buildingsScope.updateBuildings(guildId, newBuildings) shouldBe true

            client.buildingsScope.deleteBuildings(guildId, newBuildingsId) shouldBe true

            client.buildingsScope.getAllBuildingRecipes(guildId).onEach {
                newBuildingsId shouldNotContain it.name
            }
        }

        "If a non-existing building is in the batch, false is returned but the others are deleted" {
            val newBuildings = client.buildingsScope.getAllBuildingRecipes(guildId)
                .take(2)
                .map { it.copy(name = uuid(), proficiencyReduction = uuid(), bountyId = uuid()) }
                .toList()

            val newBuildingsId = newBuildings.map { it.name }

            client.buildingsScope.updateBuildings(guildId, newBuildings) shouldBe true

            client.buildingsScope.deleteBuildings(guildId, newBuildingsId + uuid()) shouldBe false

            client.buildingsScope.getAllBuildingRecipes(guildId).onEach {
                newBuildingsId shouldNotContain it.name
            }
        }

        "Cannot delete buildings from a non-existing guild" {
            shouldThrow<InvalidGuildException> {
                client.buildingsScope.deleteBuildings(uuid(), listOf(uuid(), uuid()))
            }
        }
    }

}