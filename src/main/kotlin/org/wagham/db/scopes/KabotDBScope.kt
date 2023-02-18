package org.wagham.db.scopes

import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient

interface KabotDBScope<T : Any> {

    val client: KabotMultiDBClient
    val collectionName: String

    fun getMainCollection(guildId: String): CoroutineCollection<T>

}