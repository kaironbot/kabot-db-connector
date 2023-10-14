package org.wagham.db

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.wagham.db.models.MongoCredentials

class KabotMultiDBClientTest : StringSpec() {

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

    init {
        testGeneralDbFunctionalities()
    }

    private fun StringSpec.testGeneralDbFunctionalities() {

        "Get all guilds id returns a set containing all the registered guild" {
            client.getAllGuildsId() shouldBe setOf("1099390660672503980", "867839810395176960")
        }

    }
}
