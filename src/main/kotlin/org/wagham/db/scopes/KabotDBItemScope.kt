package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.or
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.models.Item
import org.wagham.db.models.embed.LabelStub

class KabotDBItemScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Item> {

    override val collectionName = CollectionNames.ITEMS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<Item> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllItems(guildId: String) =
        getMainCollection(guildId).find().toFlow()

    fun getItems(guildId: String, labels: List<LabelStub>) =
        getMainCollection(guildId).find(
            or(
                labels.map {
                    Item::labels contains it
                }
            )
        )

    suspend fun createOrUpdateItem(guildId: String, item: Item)
            = createOrUpdateItem(guildId, listOf(item))

    suspend fun createOrUpdateItem(guildId: String, items: List<Item>) =
        getMainCollection(guildId).let { collection ->
            client.transaction(guildId) {
                val count = items.sumOf { item ->
                    val result = collection.updateOne(
                        Item::name eq item.name,
                        item,
                        UpdateOptions().upsert(true)
                    )
                    result.modifiedCount + (1.takeIf { result.upsertedId != null } ?: 0)
                }.toInt()
                count == items.size
            }.committed
        }

    suspend fun deleteItems(guildId: String, itemsId: List<String>) =
        getMainCollection(guildId).deleteMany(
            or(itemsId.map { Item::name eq it })
        ).deletedCount == itemsId.size.toLong()

}

