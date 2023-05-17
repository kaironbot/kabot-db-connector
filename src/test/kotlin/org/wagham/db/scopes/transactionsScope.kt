package org.wagham.db.scopes

import io.kotest.matchers.shouldBe
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.enums.TransactionType
import org.wagham.db.models.embed.Transaction
import org.wagham.db.uuid
import java.util.*

fun KabotMultiDBClientTest.testCharacterTransactions(
    client: KabotMultiDBClient,
    guildId: String
) {

    "Can create a new transactions list for a Character" {
        val characterId = uuid()
        val transaction = Transaction(
            date = Date(),
            target = uuid(),
            type = TransactionType.RECEIVE
        )
        client.transaction(guildId) {
            client.characterTransactionsScope.addTransactionForCharacter(
                it,
                guildId,
                characterId,
                transaction
            )
        }.committed shouldBe true
        client.characterTransactionsScope.getLastTransactions(guildId, characterId).let {
            it.size shouldBe 1
            it.first() shouldBe transaction
        }
    }

}