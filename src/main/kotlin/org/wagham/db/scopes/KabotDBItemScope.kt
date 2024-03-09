package org.wagham.db.scopes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.conversions.Bson
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.gt
import org.litote.kmongo.`in`
import org.litote.kmongo.or
import org.litote.kmongo.regex
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.models.Item
import org.wagham.db.models.client.TransactionResult
import org.wagham.db.models.embed.LabelStub
import org.wagham.db.utils.StringNormalizer

class KabotDBItemScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Item> {

    override val collectionName = CollectionNames.ITEMS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<Item> =
        client.getGuildDb(guildId).getCollection(collectionName)

    /**
     * Retrieves all [Item]s in a guild.
     *
     * @param guildId the id of the guild from which to take the items.
     * @return a [Flow] of [Item]s.
     */
    fun getAllItems(guildId: String): Flow<Item> = getMainCollection(guildId).find().toFlow()

    /**
     * Retrieves all [Item]s in a guild which id is among the one specified in [ids]. If [ids] is an empty set, then
     * an empty flow is returned.
     *
     * @param guildId the id of the guild from which to retrieve the items.
     * @param ids a [Set] containing the ids of the items to retrieve.
     * @return a [Flow] of [Item].
     */
    fun getItems(guildId: String, ids: Set<String>): Flow<Item> =
        getMainCollection(guildId).find(Item::name `in` ids).toFlow()

    /**
     * Returns all the [Item]s in a guild that have all the provided labels.
     *
     * @param guildId the guild id.
     * @param labels a [List] of [LabelStub].
     * @return a [Flow] of [Item]s, each one having all the specified labels.
     */
    fun getItems(guildId: String, labels: List<LabelStub>): Flow<Item> =
        getMainCollection(guildId).find(
            *labels.map {
                Item::labels contains it
            }.toTypedArray()
        ).toFlow()

    /**
     * Returns all the [Item]s that have all the specified [labels] and where [Item.normalizedName] starts with [query],
     * normalized using the [StringNormalizer] with pagination support.
     * If no [labels] are specified, then the [Item]s for all the labels are returned.
     * If no [query] is specified, then the [Item]s with any [Item.normalizedName] are returned.
     *
     * @param guildId the id of the guild.
     * @param labels a [List] of [LabelStub], default is [emptyList].
     * @param query the prefix to match against the item normalized name. Default is null.
     * @param skip pagination parameter: the number of sessions already provided.
     * @param limit pagination parameter: the number of session to include in one page.
     * @return a [Flow] of [Item]s.
     */
    fun getItemsMatching(
        guildId: String,
        labels: List<LabelStub> = emptyList(),
        query: String? = null,
        skip: Int? = null,
        limit: Int? = null
    ): Flow<Item> =
        getMainCollection(guildId).find(
            *labels.map {
                Item::labels contains it
            }.toTypedArray(),
            *(query?.let {
                val normalizedQuery = StringNormalizer.normalize(it)
                Item::normalizedName regex "^$normalizedQuery.*".toRegex()
            }?.let { arrayOf(it) } ?: emptyArray())
        ).let {
            if(skip != null) it.skip(skip)
            else it
        }.let {
            if(limit != null) it.limit(limit)
            else it
        }.toFlow()

    /**
     * Given an [Item] in a guild, it updates it if exists and creates it otherwise.
     *
     * @param guildId the id of the guild where to update the items.
     * @param item the [Item] to create or update.
     * @return a [TransactionResult] with the result of the operation
     */
    suspend fun createOrUpdateItem(guildId: String, item: Item): TransactionResult =
        createOrUpdateItems(guildId, listOf(item))

    /**
     * Given a list of [Item]s in a guild, creates the ones that do not exist and updates the one that already exist.
     *
     * @param guildId the id of the guild where to update the items.
     * @param items a [List] of [Item]s to create or update.
     * @return a [TransactionResult] with the result of the operation
     */
    suspend fun createOrUpdateItems(guildId: String, items: List<Item>): TransactionResult =
        getMainCollection(guildId).let { collection ->
            client.transaction(guildId) {
                val existingItemsNames = collection.find(
                    Item::name `in` items.map { it.name }
                ).toFlow().map { it.name }.toList()
                val creationResult = items.filter {
                    !existingItemsNames.contains(it.name)
                }.takeIf { it.isNotEmpty() }?.let {  itemsToCreate ->
                    collection.insertMany(itemsToCreate).insertedIds.size == itemsToCreate.size
                } ?: true

                val itemsToUpdate = items.filter { existingItemsNames.contains(it.name) }
                val updateResult = itemsToUpdate.all {
                    collection.updateOne(Item::name eq it.name, it).modifiedCount == 1L
                }
                mapOf(
                    "itemCreated" to creationResult,
                    "itemUpdated" to updateResult
                )
            }
        }

    /**
     * Retrieves all the [Item]s where the one passed as parameter is an ingredient of at least one recipe.
     *
     * @param guildId the id of the guild where to search the items.
     * @param item the [Item] that is an ingredient.
     * @return a [Flow] of [Item] where the one passes as parameter is an ingredient of at least one recipe.
     */
    fun isMaterialOf(guildId: String, item: Item): Flow<Item> = isMaterialOf(guildId, item.name)

    /**
     * Retrieves all the [Item]s where the one passed as parameter is an ingredient of at least one recipe.
     *
     * @param guildId the id of the guild where to search the items.
     * @param itemId the id of the [Item] that is an ingredient.
     * @return a [Flow] of [Item] where the one passes as parameter is an ingredient of at least one recipe.
     */
    fun isMaterialOf(guildId: String, itemId: String): Flow<Item> =
        getMainCollection(guildId)
            .find("{\"craft.materials.$itemId\": { \$exists : true }}")
            .toFlow()

    suspend fun deleteItems(guildId: String, itemsId: List<String>) =
        getMainCollection(guildId).deleteMany(
            or(itemsId.map { Item::name eq it })
        ).deletedCount == itemsId.size.toLong()

}

