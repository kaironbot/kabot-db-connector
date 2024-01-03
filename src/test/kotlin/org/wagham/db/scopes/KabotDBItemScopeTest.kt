package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.Item
import org.wagham.db.models.MongoCredentials
import org.wagham.db.models.embed.CraftRequirement
import org.wagham.db.models.embed.LabelStub
import org.wagham.db.uuid

class KabotDBItemScopeTest : StringSpec() {

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
        testItems()
    }

    private fun StringSpec.testItems() {

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

            client.itemsScope.createOrUpdateItems(guildId, itemsToUpdate).committed shouldBe true

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

            client.itemsScope.createOrUpdateItem(guildId, itemToUpdate).committed shouldBe true

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
                client.itemsScope.createOrUpdateItems(uuid(), itemsToUpdate)
            }
        }

        "Can delete items from a guild" {
            val newItems = client.itemsScope.getAllItems(guildId)
                .take(2)
                .map { it.copy(name = uuid(), link = uuid(), category = uuid()) }
                .toList()

            val newItemsId = newItems.map { it.name }

            client.itemsScope.createOrUpdateItems(guildId, newItems).committed shouldBe true

            client.itemsScope.deleteItems(guildId, newItemsId) shouldBe true

            client.itemsScope.getAllItems(guildId).onEach {
                newItemsId shouldNotContain it.name
            }
        }

        "If a non-existing item is in the batch, false is returned but the others are deleted" {
            val newItems = client.itemsScope.getAllItems(guildId)
                .take(2)
                .map { it.copy(name = uuid(), link = uuid(), category = uuid()) }
                .toList()

            val newItemsId = newItems.map { it.name }

            client.itemsScope.createOrUpdateItems(guildId, newItems).committed shouldBe true

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

        "Can get items that match one or more labels" {
            val stub = LabelStub(uuid(), uuid())
            val stub2 = LabelStub(uuid(), uuid())
            val stub3 = LabelStub(uuid(), uuid())
            val newItem = Item(
                name = uuid(),
                labels = setOf(stub, stub2, stub3)
            )
            val nonMatchedItem = Item(
                name = uuid(),
                labels = setOf(stub, stub3)
            )

            client.itemsScope.createOrUpdateItem(guildId, newItem).committed shouldBe true
            client.itemsScope.createOrUpdateItem(guildId, nonMatchedItem).committed shouldBe true
            client.itemsScope.getItems(guildId, listOf(stub, stub2)).toList().onEach {
                it.labels shouldContainAll listOf(stub, stub2)
            }.size shouldBeGreaterThan 0
        }

        "Can get all the items that have a certain ingredient" {
            val ingredient = Item(name = uuid())
            val item1 = Item(
                name = uuid(),
                craft = listOf(CraftRequirement(
                    materials = mapOf(ingredient.name to 1f)
                ))
            )
            val item2 = Item(
                name = uuid(),
                craft = listOf(CraftRequirement(
                    materials = mapOf(ingredient.name to 1f)
                ))
            )

            client.itemsScope.createOrUpdateItems(guildId, listOf(ingredient, item1, item2)).committed shouldBe true
            client.itemsScope.isMaterialOf(guildId, ingredient).toList() shouldContainExactlyInAnyOrder listOf(item1, item2)
        }

        "Can get all the items with a specified id" {
            val items = client.itemsScope.getAllItems(guildId).take(100).toList()
            client.itemsScope.getItems(guildId, items.map { it.name }.toSet()).toList() shouldContainExactlyInAnyOrder items
        }

    }
}