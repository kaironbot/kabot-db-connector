package org.wagham.db.scopes

import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient

interface KabotDBScope<T : Any> {

    val client: KabotMultiDBClient
    val collectionName: String

    /**
     * Returns the [CoroutineCollection] associated to the type [T] of the scope.
     *
     * @param guildId the id of the guild from which to take the collection.
     * @return a [CoroutineCollection] of [T].
     */
    fun getMainCollection(guildId: String): CoroutineCollection<T>

}