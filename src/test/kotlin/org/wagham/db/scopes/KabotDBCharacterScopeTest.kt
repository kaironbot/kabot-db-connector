package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.floats.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.*
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.exceptions.TransactionAbortedException
import org.wagham.db.models.Building
import org.wagham.db.models.Errata
import org.wagham.db.models.MongoCredentials
import org.wagham.db.models.creation.CharacterCreationData
import org.wagham.db.models.embed.LabelStub
import org.wagham.db.models.embed.ProficiencyStub
import org.wagham.db.uuid
import java.util.*
import kotlin.random.Random

class KabotDBCharacterScopeTest : StringSpec() {

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
        testCharacters()
        testCharactersBuildings()
        testCharactersInventories()
        testCharactersProficiencies()
    }

    private fun StringSpec.testCharactersProficiencies() {

        "Should be able to add and remove a proficiency to a Character" {
            val character = client.charactersScope.getAllCharacters(guildId).first{
                it.name.startsWith(('A'..'Z').random().toString())
            }
            val newProficiency = ProficiencyStub(uuid(), uuid())
            client.charactersScope.addProficiencyToCharacter(
                guildId,
                character.id,
                newProficiency
            ) shouldBe true

            client.charactersScope.getCharacter(guildId, character.id).proficiencies shouldContain newProficiency

            client.charactersScope.removeProficiencyFromCharacter(
                guildId,
                character.id,
                newProficiency
            ) shouldBe true

            client.charactersScope.getCharacter(guildId, character.id).proficiencies shouldNotContain newProficiency
        }

        "Adding a proficiency two times should result in a failure" {
            val character = client.charactersScope.getAllCharacters(guildId).first{
                it.name.startsWith(('A'..'Z').random().toString())
            }
            val newProficiency = ProficiencyStub(uuid(), uuid())
            client.charactersScope.addProficiencyToCharacter(
                guildId,
                character.id,
                newProficiency
            ) shouldBe true

            client.charactersScope.getCharacter(guildId, character.id).proficiencies shouldContain newProficiency

            client.charactersScope.addProficiencyToCharacter(
                guildId,
                character.id,
                newProficiency
            ) shouldBe false
        }

        "Removing a non-existent proficiency should result in a failure" {
            val character = client.charactersScope.getAllCharacters(guildId).first{
                it.name.startsWith(('A'..'Z').random().toString())
            }
            val newProficiency = ProficiencyStub(uuid(), uuid())
            client.charactersScope.removeProficiencyFromCharacter(
                guildId,
                character.id,
                newProficiency
            ) shouldBe false

            client.charactersScope.getCharacter(guildId, character.id).proficiencies shouldNotContain newProficiency
        }

        "Should be able to add and remove a language to a Character" {
            val character = client.charactersScope.getAllCharacters(guildId).first{
                it.name.startsWith(('A'..'Z').random().toString())
            }
            val newLanguage = ProficiencyStub(uuid(), uuid())
            client.transaction(guildId) {
                client.charactersScope.addLanguageToCharacter(it, guildId, character.id, newLanguage)
            }.committed shouldBe true

            client.charactersScope.getCharacter(guildId, character.id).languages shouldContain newLanguage

            client.transaction(guildId) {
                client.charactersScope.removeLanguageFromCharacter(it, guildId, character.id, newLanguage)
            }

            client.charactersScope.getCharacter(guildId, character.id).languages shouldNotContain newLanguage
        }

        "Adding a language two times should result in a failure" {
            val character = client.charactersScope.getAllCharacters(guildId).first{
                it.name.startsWith(('A'..'Z').random().toString())
            }
            val newLanguage = ProficiencyStub(uuid(), uuid())

            client.transaction(guildId) {
                client.charactersScope.addLanguageToCharacter(it, guildId, character.id, newLanguage)
            }.committed shouldBe true


            client.charactersScope.getCharacter(guildId, character.id).languages shouldContain newLanguage

            client.transaction(guildId) {
                client.charactersScope.addLanguageToCharacter(it, guildId, character.id, newLanguage)
                //it.tryCommit("add language", false)
            }.committed shouldBe false

        }

        "Removing a non-existent language should result in a failure" {
            val character = client.charactersScope.getAllCharacters(guildId).first{
                it.name.startsWith(('A'..'Z').random().toString())
            }
            val newLanguage = ProficiencyStub(uuid(), uuid())
            client.transaction(guildId) {
                client.charactersScope.removeLanguageFromCharacter(it, guildId, character.id, newLanguage)
                // it.tryCommit("remove language", false)
            }.committed shouldBe false

            client.charactersScope.getCharacter(guildId, character.id).languages shouldNotContain newLanguage
        }
    }

    private fun StringSpec.testCharactersInventories() {

        "removeItemFromInventory should be able of removing an item from a character inventory" {
            val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() && it.inventory.keys.firstOrNull{ c -> it.inventory[c]!! > 1 } != null}
            val itemToRemove = character.inventory.keys.first{ character.inventory[it]!! > 1 }
            val otherItem = (character.inventory.keys - itemToRemove).random()
            val result = client.transaction(guildId) {
                client.charactersScope.removeItemFromInventory(it, guildId, character.id, itemToRemove, 1)
            }
            result.committed shouldBe true
            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.inventory[itemToRemove] shouldBe (character.inventory[itemToRemove]!! - 1)
            updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
        }

        "removeItemFromInventory should be able of removing all types of an item from a character inventory" {
            val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() && it.inventory.keys.firstOrNull{ c -> it.inventory[c]!! > 1 } != null}
            val itemToRemove = character.inventory.keys.first{ character.inventory[it]!! > 1 }
            val otherItem = (character.inventory.keys - itemToRemove).random()
            val result = client.transaction(guildId) {
                client.charactersScope.removeItemFromInventory(it, guildId, character.id, itemToRemove, character.inventory[itemToRemove]!!)
            }
            result.committed shouldBe true
            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.inventory.keys shouldNotContain itemToRemove
            updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
        }

        "removeItemFromInventory should be able of removing all types of an item from a character inventory by removing more than the existing quantity" {
            val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() && it.inventory.keys.firstOrNull{ c -> it.inventory[c]!! > 1 } != null}
            val itemToRemove = character.inventory.keys.first{ character.inventory[it]!! > 1 }
            val otherItem = (character.inventory.keys - itemToRemove).random()
            val result = client.transaction(guildId) {
                client.charactersScope.removeItemFromInventory(it, guildId, character.id, itemToRemove, character.inventory[itemToRemove]!! + 2)
            }
            result.committed shouldBe true
            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.inventory.keys shouldNotContain itemToRemove
            updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
        }

        "removeItemFromInventory should not be able of removing an item the character does not have" {
            val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() }
            val itemToRemove = UUID.randomUUID().toString()
            val otherItem = (character.inventory.keys - itemToRemove).random()
            val result = client.transaction(guildId) {
                client.charactersScope.removeItemFromInventory(it, guildId, character.id, itemToRemove, 1)
            }
            result.committed shouldBe false
            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.inventory.keys shouldNotContain itemToRemove
            updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
        }

        "addItemToInventory should be able of adding a new item to a character inventory" {
            val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() }
            val item = uuid()
            val qty = Random.nextInt(0, 100)
            val result = client.transaction(guildId) {
                client.charactersScope.addItemToInventory(it, guildId, character.id, item, qty)
            }
            result.committed shouldBe true
            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.inventory[item] shouldBe qty
        }

        "addItemFromInventory should be able of adding a quantity of an item to a character inventory" {
            val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() && it.inventory.keys.firstOrNull{ c -> it.inventory[c]!! > 1 } != null}
            val itemToAdd = character.inventory.keys.first{ character.inventory[it]!! > 1 }
            val qty = Random.nextInt(0, 100)
            val otherItem = (character.inventory.keys - itemToAdd).random()
            val result = client.transaction(guildId) {
                client.charactersScope.addItemToInventory(it, guildId, character.id, itemToAdd, qty)
            }
            result.committed shouldBe true
            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.inventory[itemToAdd] shouldBe (character.inventory[itemToAdd]!! + qty)
            updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
        }

        "removeItemFromAllInventories should be able of removing an item from all inventories" {
            val characters = client.charactersScope.getAllCharacters(guildId).filter { it.inventory.isNotEmpty() }.take(10).toList()
            val item = uuid()
            val qty = Random.nextInt(0, 100)
            val result = client.transaction(guildId) {
                characters.forEach { character ->
                    client.charactersScope.addItemToInventory(it, guildId, character.id, item, qty)
                }
            }
            result.committed shouldBe true

            client.transaction(guildId) {
                client.charactersScope.removeItemFromAllInventories(it, guildId, item)
            }.committed shouldBe true

            characters.forEach {
                client.charactersScope.getCharacter(guildId, it.id).let { c ->
                    c.inventory shouldNotContainKey item
                }
            }
        }

        "renameItemInAllInventories should be able of renaming items in all inventories" {
            val characters = client.charactersScope.getAllCharacters(guildId).filter { it.inventory.isNotEmpty() }.take(10).toList()
            val item = "${uuid()}_start"
            val newName = "${uuid()}_renamed"
            val qty = Random.nextInt(0, 100)
            val result = client.transaction(guildId) {
                characters.forEach { character ->
                    client.charactersScope.addItemToInventory(it, guildId, character.id, item, qty)
                }
            }
            result.committed shouldBe true

            client.transaction(guildId) {
                client.charactersScope.renameItemInAllInventories(it, guildId, item, newName)
                mapOf("test" to true)
            }.committed shouldBe true

            characters.forEach {
                client.charactersScope.getCharacter(guildId, it.id).let { c ->
                    c.inventory shouldNotContainKey item
                    c.inventory shouldContainKey newName
                }
            }
        }

    }

    private fun StringSpec.testCharacters() {

        "getAllCharacters should be able of getting all the characters" {
            client.charactersScope.getAllCharacters(guildId).count() shouldBeGreaterThan 0
        }

        "getAllCharacters should be able of getting all the characters with a certain status" {
            client.charactersScope.getAllCharacters(guildId, CharacterStatus.active)
                .onEach {
                    it.status shouldBe CharacterStatus.active
                }.count() shouldBeGreaterThan 0
        }

        "getActiveCharacter should be able to get the active character for a player" {
            val anActiveCharacter = client.charactersScope.getAllCharacters(guildId).firstOrNull{ it.status == CharacterStatus.active}.shouldNotBeNull()
            val activeCharacter = client.charactersScope.getActiveCharacterOrAllActive(guildId, anActiveCharacter.player)
                .currentActive.shouldNotBeNull()
            activeCharacter shouldNotBe null
            activeCharacter.name shouldBe anActiveCharacter.name
            activeCharacter.player shouldBe anActiveCharacter.player
            activeCharacter.race shouldBe anActiveCharacter.race
            activeCharacter.territory shouldBe anActiveCharacter.territory
            activeCharacter.status shouldBe CharacterStatus.active
        }

        "Should be possible to update a character" {
            val anActiveCharacter = client.charactersScope
                .getAllCharacters(guildId)
                .filter{ it.status == CharacterStatus.active}
                .take(1000)
                .toList()
                .random()

            val newRace = uuid()
            val newClass = uuid()
            val updatedCharacter = anActiveCharacter.copy(
                race = newRace,
                characterClass = anActiveCharacter.characterClass + newClass,
                labels = anActiveCharacter.labels + LabelStub(uuid(), uuid())
            )
            client.charactersScope.updateCharacter(guildId, updatedCharacter)
            client.charactersScope.getCharacter(guildId, anActiveCharacter.id) shouldBe updatedCharacter
        }

        "ms function should be able to get the correct ms for a player" {
            val c = client.charactersScope.getAllCharacters(guildId).firstOrNull{ it.status == CharacterStatus.active}
            c shouldNotBe null
            c!!.ms() shouldBe listOf(
                c.sessionMS,
                c.errataMS,
                c.pbcMS,
                c.masterMS
            ).sum()
        }

        "getActiveCharacter should not be able to get data for a non existing player" {
            client.charactersScope.getActiveCharacters(guildId, "I_DO_NOT_EXIST").count() shouldBe 0
        }

        "getCharacters can get all the characters with a specified id" {
            val characters = client.charactersScope.getAllCharacters(guildId).take(1000).toList().let { characters ->
                List(10) { characters.random() }
            }

            val retrievedCharacters = client.charactersScope.getCharacters(guildId, characters.map { it.id } + List(10) { uuid() }).toList()
            retrievedCharacters shouldContainExactlyInAnyOrder characters
        }

        "getCharactersWithPlayer should be able to get data for all the characters if no parameter is passed" {
            client.charactersScope.getCharactersWithPlayer(guildId).count() shouldBeGreaterThan 0
        }

        "getCharactersWithPlayer should be able to get data for all the characters with a parameter" {
            client.charactersScope.getCharactersWithPlayer(guildId, CharacterStatus.active)
                .onEach {
                    it.status shouldBe CharacterStatus.active
                }
                .count() shouldBeGreaterThan 0
        }

        "getCharacter should throw an exception when trying to get a non-existing character" {
            shouldThrow<ResourceNotFoundException> {
                client.charactersScope.getCharacter(guildId, "I_DO_NOT_EXIST")
            }
        }

        "getCharacter should be able to get an existing character" {
            val aRandomCharacter = client.charactersScope.getAllCharacters(guildId).take(100).toList().random()
            val fetchedCharacter = client.charactersScope.getCharacter(guildId, aRandomCharacter.id)
            aRandomCharacter.name shouldBe fetchedCharacter.name
            aRandomCharacter.characterClass shouldBe fetchedCharacter.characterClass
        }

        "subtractMoney should be able of subtracting money from a character" {
            val character = client.charactersScope.getAllCharacters(guildId).first { it.money > 0 }
            val result = client.transaction(guildId) {
                client.charactersScope.subtractMoney(it, guildId, character.id, character.money)
            }
            result.committed shouldBe true
            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.money shouldBe 0
        }

        "addMoney should be able of add money from a character" {
            val character = client.charactersScope.getAllCharacters(guildId).take(1000).toList().random()
            val amount = Random.nextFloat() * 1000
            val result = client.transaction(guildId) {
                client.charactersScope.addMoney(it, guildId, character.id, amount)
            }
            result.committed shouldBe true
            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.money shouldBe character.money + amount
        }

        "All modifications should be preserved in a session but discarded if false is returned" {
            val character = client.charactersScope.getAllCharacters(guildId).first { it.money > 0 }
            val result = client.transaction(guildId) {
                client.charactersScope.subtractMoney(it, guildId, character.id, character.money)
                client.charactersScope.getCharacter(it.session, guildId, character.id).money shouldBe 0
                it.tryCommit("test", false)
            }
            result.committed shouldBe false
            result.exception.shouldBeInstanceOf<TransactionAbortedException>()
            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.money shouldBeGreaterThan 0f
        }

        "All modifications should be preserved in a session but discarded if an exception is thrown" {
            val character = client.charactersScope.getAllCharacters(guildId).first { it.money > 0 }
            val newProficiency = ProficiencyStub(uuid(), uuid())
            val result = client.transaction(guildId) {
                client.charactersScope.addProficiencyToCharacter(it, guildId, character.id, newProficiency)
                client.charactersScope.getCharacter(it.session, guildId, character.id).proficiencies shouldContain newProficiency
                throw IllegalArgumentException("I do not like it")
            }
            result.committed shouldBe false
            result.exception.shouldBeInstanceOf<IllegalArgumentException>()
            result.exception?.message shouldBe "I do not like it"
            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.proficiencies shouldNotContain newProficiency
        }

        "Should be able to create a Character for an existing player" {
            val playerId = uuid()
            client.transaction(guildId) {
                client.playersScope.createPlayer(it, guildId, playerId, playerId)
            }.committed shouldBe true
            val player = client.playersScope.getPlayer(guildId, playerId).shouldNotBeNull()
            val data = CharacterCreationData(
                name = uuid(),
                startingLevel = "1",
                race = uuid(),
                characterClass = uuid(),
                null,
                null
            )
            client.charactersScope.createCharacter(
                guildId,
                player.playerId,
                player.name,
                data
            ).committed shouldBe true

            val character = client.charactersScope.getCharacter(guildId, "${player.playerId}:${data.name}")
            val expTable = client.utilityScope.getExpTable(guildId)
            character.name shouldBe data.name
            character.race shouldBe data.race
            character.characterClass shouldBe data.characterClass?.let { listOf(it) }
            character.territory shouldBe null
            character.ms() shouldBe expTable.levelToExp(data.startingLevel)
        }

        "Should be able to create a Character for a non-existing player" {
            val playerId = uuid()
            val playerName = uuid()
            val data = CharacterCreationData(
                name = uuid(),
                startingLevel = "${Random.nextInt(2, 10)}",
                race = uuid(),
                characterClass = uuid(),
                null,
                null
            )
            client.charactersScope.createCharacter(
                guildId,
                playerId,
                playerName,
                data
            ).committed shouldBe true

            val player = client.playersScope.getPlayer(guildId, playerId).shouldNotBeNull()
            player.name shouldBe playerName

            val character = client.charactersScope.getCharacter(guildId, "${player.playerId}:${data.name}")
            val expTable = client.utilityScope.getExpTable(guildId)
            character.name shouldBe data.name
            character.race shouldBe data.race
            character.characterClass shouldBe data.characterClass?.let { listOf(it) }
            character.territory shouldBe null
            character.ms() shouldBe expTable.levelToExp(data.startingLevel)
        }

        "getActiveCharacter should return an empty flow if the player has no active character" {
            val playerId = uuid()
            client.transaction(guildId) {
                client.playersScope.createPlayer(it, guildId, playerId, playerId)
            }.committed shouldBe true
            client.charactersScope.getActiveCharacters(guildId, playerId).count() shouldBe 0
        }

        "getActiveCharacter should be able to return all the active characters for a player if no active is specified" {
            val playerId = uuid()
            val playerName = uuid()
            val size = 3
            val characters = List(size) {
                CharacterCreationData(
                    name = uuid(),
                    startingLevel = "${Random.nextInt(2, 10)}",
                    race = uuid(),
                    characterClass = uuid(),
                    null,
                    null
                )
            }.onEach { data ->
                client.charactersScope.createCharacter(
                    guildId,
                    playerId,
                    playerName,
                    data
                ).committed shouldBe true
            }

            client.charactersScope.getActiveCharacterOrAllActive(guildId, playerId).let {
                it.currentActive shouldBe null
                it.allActive.size shouldBeGreaterThan 0
                it.allActive
            }.map { character ->
                characters.first { it.name == character.name }.let { ccd ->
                    character.race shouldBe ccd.race
                    character.characterClass shouldBe ccd.characterClass?.let { listOf(it) }
                    character.age shouldBe null
                    character.territory shouldBe null
                }
            }.count() shouldBe size
        }

        "getActiveCharacter should return the active characters if specified on the player" {
            val playerId = uuid()
            val playerName = uuid()
            val size = 3
            val characters = List(size) {
                CharacterCreationData(
                    name = uuid(),
                    startingLevel = "${Random.nextInt(2, 10)}",
                    race = uuid(),
                    characterClass = uuid(),
                    null,
                    null
                )
            }.onEach { data ->
                client.charactersScope.createCharacter(
                    guildId,
                    playerId,
                    playerName,
                    data
                ).committed shouldBe true
            }
            val activeCharacter = characters.first()
            client.playersScope.setActiveCharacter(guildId, playerId, "$playerId:${activeCharacter.name}") shouldBe true

            client.charactersScope.getActiveCharacterOrAllActive(guildId, playerId).let {
                it.allActive.size shouldBe 0
                it.currentActive.shouldNotBeNull()
            }.let { character ->
                character.race shouldBe activeCharacter.race
                character.characterClass shouldBe activeCharacter.characterClass?.let { listOf(it) }
                character.age shouldBe null
                character.territory shouldBe null
            }
        }

        "Should be able of updating a character ms and status using an errata" {
            val character = client.charactersScope.getAllCharacters(guildId).take(1000).toList().random()

            val errata = Errata(
                ms = Random.nextInt(1, 10),
                date = Date(),
                statusChange = CharacterStatus.entries.random()
            )

            client.charactersScope.addErrata(guildId, character.id, errata).committed shouldBe true

            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.errataMS shouldBe (character.errataMS + errata.ms)
            updatedCharacter.status shouldBe errata.statusChange
            updatedCharacter.errata shouldContain errata
        }

        "Should be able of updating a character ms using an errata" {
            val character = client.charactersScope.getAllCharacters(guildId).take(1000).toList().random()

            val errata = Errata(
                ms = Random.nextInt(1, 10),
                date = Date()
            )

            client.charactersScope.addErrata(guildId, character.id, errata).committed shouldBe true

            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.errataMS shouldBe (character.errataMS + errata.ms)
            updatedCharacter.status shouldBe character.status
            updatedCharacter.errata shouldContain errata
        }

    }

    private fun StringSpec.testCharactersBuildings() {

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

}