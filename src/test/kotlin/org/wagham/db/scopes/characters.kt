package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.floats.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.*
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.exceptions.NoActiveCharacterException
import org.wagham.db.exceptions.ResourceNotFoundException
import java.util.UUID

fun KabotMultiDBClientTest.testCharacters(
    client: KabotMultiDBClient,
    guildId: String
) {

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
        val anActiveCharacter = client.charactersScope.getAllCharacters(guildId).firstOrNull{ it.status == CharacterStatus.active}
        anActiveCharacter shouldNotBe null
        val activeCharacter = client.charactersScope.getActiveCharacter(guildId, anActiveCharacter!!.player)
        activeCharacter shouldNotBe null
        activeCharacter.name shouldBe anActiveCharacter.name
        activeCharacter.player shouldBe anActiveCharacter.player
        activeCharacter.race shouldBe anActiveCharacter.race
        activeCharacter.territory shouldBe anActiveCharacter.territory
        activeCharacter.status shouldBe CharacterStatus.active
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
        shouldThrow<NoActiveCharacterException> {
            client.charactersScope.getActiveCharacter(guildId, "I_DO_NOT_EXIST")
        }
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

    "Should be able to add and remove a proficiency to a Character" {
        val character = client.charactersScope.getAllCharacters(guildId).first{
            it.name.startsWith(('A'..'Z').random().toString())
        }
        val newProficiency = UUID.randomUUID().toString()
        client.charactersScope.addProficiencyToCharacter(
            guildId,
            character.name,
            newProficiency
        ) shouldBe true

        client.charactersScope.getCharacter(guildId, character.name).proficiencies shouldContain newProficiency

        client.charactersScope.removeProficiencyFromCharacter(
            guildId,
            character.name,
            newProficiency
        ) shouldBe true

        client.charactersScope.getCharacter(guildId, character.name).proficiencies shouldNotContain newProficiency
    }

    "Adding a proficiency two times should result in a failure" {
        val character = client.charactersScope.getAllCharacters(guildId).first{
            it.name.startsWith(('A'..'Z').random().toString())
        }
        val newProficiency = UUID.randomUUID().toString()
        client.charactersScope.addProficiencyToCharacter(
            guildId,
            character.name,
            newProficiency
        ) shouldBe true

        client.charactersScope.getCharacter(guildId, character.name).proficiencies shouldContain newProficiency

        client.charactersScope.addProficiencyToCharacter(
            guildId,
            character.name,
            newProficiency
        ) shouldBe false
    }

    "Removing a non-existent proficiency should result in a failure" {
        val character = client.charactersScope.getAllCharacters(guildId).first{
            it.name.startsWith(('A'..'Z').random().toString())
        }
        val newProficiency = UUID.randomUUID().toString()
        client.charactersScope.removeProficiencyFromCharacter(
            guildId,
            character.name,
            newProficiency
        ) shouldBe false

        client.charactersScope.getCharacter(guildId, character.name).proficiencies shouldNotContain newProficiency
    }

    "getCharacter should throw an exception when trying to get a non-existing character" {
        shouldThrow<ResourceNotFoundException> {
            client.charactersScope.getCharacter(guildId, "I_DO_NOT_EXIST")
        }
    }

    "getCharacter should be able to get an existing character" {
        val aRandomCharacter = client.charactersScope.getAllCharacters(guildId).take(100).toList().random()
        val fetchedCharacter = client.charactersScope.getCharacter(guildId, aRandomCharacter.name)
        aRandomCharacter.name shouldBe fetchedCharacter.name
        aRandomCharacter.characterClass shouldBe fetchedCharacter.characterClass
    }

    "subtractMoney should be able of subtracting money from a character" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.money > 0 }
        client.transaction(guildId) {
            client.charactersScope.subtractMoney(it, guildId, character.name, character.money) shouldBe true
            true
        }
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.money shouldBe 0
    }

    "All modifications should be preserved in a session but discarded if false is returned" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.money > 0 }
        client.transaction(guildId) {
            client.charactersScope.subtractMoney(it, guildId, character.name, character.money) shouldBe true
            client.charactersScope.getCharacter(it, guildId, character.name).money shouldBe 0
            false
        }
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.money shouldBeGreaterThan 0f
    }

    "All modifications should be preserved in a session but discarded if an exception is thrown" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.money > 0 }
        val newProficiency = UUID.randomUUID().toString()
        client.transaction(guildId) {
            client.charactersScope.addProficiencyToCharacter(it, guildId, character.name, newProficiency) shouldBe true
            client.charactersScope.getCharacter(it, guildId, character.name).proficiencies shouldContain newProficiency
            throw Exception("I do not like it")
        }
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.proficiencies shouldNotContain newProficiency
    }

    "removeItemFromInventory should be able of removing an item from a character inventory" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() }
        val itemToRemove = character.inventory.keys.first{ character.inventory[it]!! > 1 }
        val otherItem = (character.inventory.keys - itemToRemove).random()
        client.transaction(guildId) {
            client.charactersScope.removeItemFromInventory(it, guildId, character.name, itemToRemove, 1) shouldBe true
            true
        }
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.inventory[itemToRemove] shouldBe (character.inventory[itemToRemove]!! - 1)
        updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
    }

    "removeItemFromInventory should be able of removing all types of an item from a character inventory" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() }
        val itemToRemove = character.inventory.keys.first{ character.inventory[it]!! > 1 }
        val otherItem = (character.inventory.keys - itemToRemove).random()
        client.transaction(guildId) {
            client.charactersScope.removeItemFromInventory(it, guildId, character.name, itemToRemove, character.inventory[itemToRemove]!!) shouldBe true
            true
        }
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.inventory.keys shouldNotContain itemToRemove
        updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
    }

    "removeItemFromInventory should be able of removing all types of an item from a character inventory by removing more than the existing quantity" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() }
        val itemToRemove = character.inventory.keys.first{ character.inventory[it]!! > 1 }
        val otherItem = (character.inventory.keys - itemToRemove).random()
        client.transaction(guildId) {
            client.charactersScope.removeItemFromInventory(it, guildId, character.name, itemToRemove, character.inventory[itemToRemove]!! + 2) shouldBe true
            true
        }
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.inventory.keys shouldNotContain itemToRemove
        updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
    }

    "removeItemFromInventory should not be able of removing an item the character does not have" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() }
        val itemToRemove = UUID.randomUUID().toString()
        val otherItem = (character.inventory.keys - itemToRemove).random()
        client.transaction(guildId) {
            client.charactersScope.removeItemFromInventory(it, guildId, character.name, itemToRemove, 1) shouldBe false
            true
        }
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.inventory.keys shouldNotContain itemToRemove
        updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
    }

}