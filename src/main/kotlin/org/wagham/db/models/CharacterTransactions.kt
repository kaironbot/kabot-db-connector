package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.models.embed.Transaction

data class CharacterTransactions (
    @BsonId val character: String,
    val transactions: List<Transaction> = emptyList()
) {

    fun addTransaction(newTransaction: Transaction) = this.copy(
        transactions = (transactions + newTransaction).sortedByDescending { it.date }
    )

}