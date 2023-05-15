package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.uuid

fun KabotMultiDBClientTest.testItems(
    client: KabotMultiDBClient,
    guildId: String
) {

    /*
    "getAllItems should be able to get all the items" {
        client.itemsScope.getAllItems(guildId).count() shouldBeGreaterThan 0
    }

    "Cannot get items from a non existent guild" {
        shouldThrow<InvalidGuildException> {
            client.itemsScope.getAllItems("I_DO_NOT_EXIST")
        }
    }

    "Should be able of updating a list of items" {
        val itemsToUpdate = client.itemsScope.getAllItems(guildId)
            .take(3)
            .map { it.copy(link = uuid(), category = uuid()) }
            .toList()

        client.itemsScope.updateItems(guildId, itemsToUpdate) shouldBe true

        client.itemsScope.getAllItems(guildId)
            .filter { i -> itemsToUpdate.map { it.name }.contains(i.name) }
            .onEach { i ->
                val oldItem = itemsToUpdate.first{ it.name == i.name }
                i.link shouldBe oldItem.link
                i.category shouldBe oldItem.category
            }.collect()
    }

    "If an item does not exist, it is inserted" {
        val itemToUpdate = client.itemsScope.getAllItems(guildId)
            .first()
            .copy(
                name = uuid(),
                link = uuid(),
                category = uuid()
            )

        client.itemsScope.updateItem(guildId, itemToUpdate) shouldBe true

        client.itemsScope.getAllItems(guildId)
            .first { it.name == itemToUpdate.name }
            .let {
                it.category shouldBe itemToUpdate.category
                it.link shouldBe itemToUpdate.link
            }
    }

    "Cannot update items from a non existent guild" {
        val itemsToUpdate = client.itemsScope.getAllItems(guildId).toList()
        shouldThrow<InvalidGuildException> {
            client.itemsScope.updateItems(uuid(), itemsToUpdate)
        }
    }

    "Can delete items from a guild" {
        val newItems = client.itemsScope.getAllItems(guildId)
            .take(2)
            .map { it.copy(name = uuid(), link = uuid(), category = uuid()) }
            .toList()

        val newItemsId = newItems.map { it.name }

        client.itemsScope.updateItems(guildId, newItems) shouldBe true

        client.itemsScope.deleteItems(guildId, newItemsId) shouldBe true

        client.itemsScope.getAllItems(guildId).onEach {
            newItemsId shouldNotContain it.name
        }
    }

    "If a non-existing building is in the batch, false is returned but the others are deleted" {
        val newItems = client.itemsScope.getAllItems(guildId)
            .take(2)
            .map { it.copy(name = uuid(), link = uuid(), category = uuid()) }
            .toList()

        val newItemsId = newItems.map { it.name }

        client.itemsScope.updateItems(guildId, newItems) shouldBe true

        client.itemsScope.deleteItems(guildId, newItemsId + uuid()) shouldBe false

        client.itemsScope.getAllItems(guildId).onEach {
            newItemsId shouldNotContain it.name
        }
    }

    "Cannot delete items from a non-existing guild" {
        shouldThrow<InvalidGuildException> {
            client.itemsScope.deleteItems(uuid(), listOf(uuid(), uuid()))
        }
    }
*/
}