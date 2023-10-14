package org.wagham.db.scopes

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.TransactionType
import org.wagham.db.models.MongoCredentials
import org.wagham.db.models.embed.Transaction
import org.wagham.db.uuid
import java.util.*

class KabotDBCharacterTransactionsScopeTest : StringSpec() {

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
        testCharacterTransactions()
    }

    private fun StringSpec.testCharacterTransactions() {

        "Can create a new transactions list for a Character" {
            val characterId = uuid()
            val transaction = Transaction(
                date = Date(),
                otherParty = uuid(),
                operation = uuid(),
                type = TransactionType.ADD
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

}