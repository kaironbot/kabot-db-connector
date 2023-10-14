package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.MongoCredentials
import org.wagham.db.uuid

class KabotDBBackgroundScopeTest : StringSpec() {

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
        testBackgrounds()
    }

    private fun StringSpec.testBackgrounds() {
        "getAllBackgrounds should be able to get all the backgrounds" {
            val backgrounds = client.backgroundsScope.getAllBackgrounds(guildId)
            backgrounds.count() shouldBeGreaterThan 0
        }

        "getAllBackgrounds should not be able of getting the backgrounds for a non-existent guild" {
            shouldThrow<InvalidGuildException> {
                client.backgroundsScope.getAllBackgrounds(uuid())
            }
        }

        "Should be able of rewriting the whole background collection" {
            val backgrounds = client.backgroundsScope.getAllBackgrounds(guildId).toList()
            val backgroundToEdit = backgrounds.random().copy(
                race = uuid(),
                link = uuid(),
            )
            client.backgroundsScope.rewriteAllBackgrounds(
                guildId,
                backgrounds.filter { it.name != backgroundToEdit.name } + backgroundToEdit
            ) shouldBe true
            val newBackgrounds = client.backgroundsScope.getAllBackgrounds(guildId).toList()
            newBackgrounds.size shouldBe backgrounds.size
            newBackgrounds.first { it.name == backgroundToEdit.name }.let {
                it.race shouldBe backgroundToEdit.race
                it.link shouldBe backgroundToEdit.link
            }
        }

        "Should not be able of updating the backgrounds for a non-existent guild" {
            val backgrounds = client.backgroundsScope.getAllBackgrounds(guildId).toList()
            shouldThrow<InvalidGuildException> {
                client.backgroundsScope.rewriteAllBackgrounds(
                    uuid(),
                    backgrounds
                )
            }
        }
    }
}