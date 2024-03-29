package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.models.CharacterTransactions
import org.wagham.db.models.client.KabotSession
import org.wagham.db.models.embed.Transaction
import org.wagham.db.utils.isSuccessful
import org.wagham.db.utils.subList

class KabotDBCharacterTransactionsScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<CharacterTransactions> {

    override val collectionName = CollectionNames.CHARACTER_TRANSACTIONS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<CharacterTransactions> =
        client.getGuildDb(guildId).getCollection(collectionName)

    suspend fun addTransactionForCharacter(session: KabotSession, guildId: String, characterId: String, transaction: Transaction) {
        getMainCollection(guildId).let { collection ->
            val realTransaction = collection.findOne(session.session, CharacterTransactions::character eq characterId)
                ?: CharacterTransactions(characterId)
            collection.updateOne(
                session.session,
                CharacterTransactions::character eq characterId,
                realTransaction.addTransaction(transaction),
                UpdateOptions().upsert(true)
            ).isSuccessful().also {
                session.tryCommit("add transaction", it)
            }
        }
    }


    suspend fun getLastTransactions(guildId: String, characterId: String, limit: Int? = null) =
        getMainCollection(guildId)
            .findOne(CharacterTransactions::character eq characterId)
            ?.transactions
            ?.subList(0, limit) ?: emptyList()

}