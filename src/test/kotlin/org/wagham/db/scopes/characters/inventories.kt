package org.wagham.db.scopes.characters

import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.uuid
import java.util.*
import kotlin.random.Random

fun KabotMultiDBClientTest.testCharactersInventories(
    client: KabotMultiDBClient,
    guildId: String
) {

    "removeItemFromInventory should be able of removing an item from a character inventory" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() && it.inventory.keys.firstOrNull{ c -> it.inventory[c]!! > 1 } != null}
        val itemToRemove = character.inventory.keys.first{ character.inventory[it]!! > 1 }
        val otherItem = (character.inventory.keys - itemToRemove).random()
        val result = client.transaction(guildId) {
            client.charactersScope.removeItemFromInventory(it, guildId, character.name, itemToRemove, 1) shouldBe true
            true
        }
        result.committed shouldBe true
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.inventory[itemToRemove] shouldBe (character.inventory[itemToRemove]!! - 1)
        updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
    }

    "removeItemFromInventory should be able of removing all types of an item from a character inventory" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() && it.inventory.keys.firstOrNull{ c -> it.inventory[c]!! > 1 } != null}
        val itemToRemove = character.inventory.keys.first{ character.inventory[it]!! > 1 }
        val otherItem = (character.inventory.keys - itemToRemove).random()
        val result = client.transaction(guildId) {
            client.charactersScope.removeItemFromInventory(it, guildId, character.name, itemToRemove, character.inventory[itemToRemove]!!) shouldBe true
            true
        }
        result.committed shouldBe true
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.inventory.keys shouldNotContain itemToRemove
        updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
    }

    "removeItemFromInventory should be able of removing all types of an item from a character inventory by removing more than the existing quantity" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() && it.inventory.keys.firstOrNull{ c -> it.inventory[c]!! > 1 } != null}
        val itemToRemove = character.inventory.keys.first{ character.inventory[it]!! > 1 }
        val otherItem = (character.inventory.keys - itemToRemove).random()
        val result = client.transaction(guildId) {
            client.charactersScope.removeItemFromInventory(it, guildId, character.name, itemToRemove, character.inventory[itemToRemove]!! + 2) shouldBe true
            true
        }
        result.committed shouldBe true
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.inventory.keys shouldNotContain itemToRemove
        updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
    }

    "removeItemFromInventory should not be able of removing an item the character does not have" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() }
        val itemToRemove = UUID.randomUUID().toString()
        val otherItem = (character.inventory.keys - itemToRemove).random()
        val result = client.transaction(guildId) {
            client.charactersScope.removeItemFromInventory(it, guildId, character.name, itemToRemove, 1) shouldBe false
            true
        }
        result.committed shouldBe true
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.inventory.keys shouldNotContain itemToRemove
        updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
    }

    "addItemToInventory should be able of adding a new item to a character inventory" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() }
        val item = uuid()
        val qty = Random.nextInt(0, 100)
        val result = client.transaction(guildId) {
            client.charactersScope.addItemToInventory(it, guildId, character.name, item, qty) shouldBe true
            true
        }
        result.committed shouldBe true
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.inventory[item] shouldBe qty
    }

    "addItemFromInventory should be able of adding a quantity of an item to a character inventory" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.inventory.isNotEmpty() && it.inventory.keys.firstOrNull{ c -> it.inventory[c]!! > 1 } != null}
        val itemToAdd = character.inventory.keys.first{ character.inventory[it]!! > 1 }
        val qty = Random.nextInt(0, 100)
        val otherItem = (character.inventory.keys - itemToAdd).random()
        val result = client.transaction(guildId) {
            client.charactersScope.addItemToInventory(it, guildId, character.name, itemToAdd, qty) shouldBe true
            true
        }
        result.committed shouldBe true
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.name)
        updatedCharacter.inventory[itemToAdd] shouldBe (character.inventory[itemToAdd]!! + qty)
        updatedCharacter.inventory[otherItem] shouldBe character.inventory[otherItem]
    }

    "removeItemFromAllInventories should be able of removing an item from all inventoried" {
        val characters = client.charactersScope.getAllCharacters(guildId).filter { it.inventory.isNotEmpty() }.take(10).toList()
        val item = uuid()
        val qty = Random.nextInt(0, 100)
        val result = client.transaction(guildId) {
            characters.forEach { character ->
                client.charactersScope.addItemToInventory(it, guildId, character.name, item, qty) shouldBe true
            }
            true
        }
        result.committed shouldBe true

        client.transaction(guildId) {
            client.charactersScope.removeItemFromAllInventories(it, guildId, item)
        }.committed shouldBe true

        characters.forEach {
            client.charactersScope.getCharacter(guildId, it.name).let { c ->
                c.inventory shouldNotContainKey item
            }
        }
    }

}