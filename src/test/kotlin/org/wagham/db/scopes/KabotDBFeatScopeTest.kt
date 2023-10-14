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

class KabotDBFeatScopeTest : StringSpec() {

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
        testFeats()
    }

    private fun StringSpec.testFeats() {

        "getAllFeats should be able to get all the feats" {
            val feats = client.featsScope.getAllFeats(guildId)
            feats.count() shouldBeGreaterThan 0
        }

        "getAllFeats should not be able of getting all the feats from a non-existing collection" {
            shouldThrow<InvalidGuildException> {
                client.featsScope.getAllFeats(uuid())
            }
        }

        "Should be able of rewriting the whole feats collection" {
            val feats = client.featsScope.getAllFeats(guildId).toList()
            val featToEdit = feats.random().copy(
                link = uuid(),
                source = uuid()
            )
            client.featsScope.rewriteAllFeats(
                guildId,
                feats.filter { it.name != featToEdit.name } + featToEdit
            ) shouldBe true
            val newFeats = client.featsScope.getAllFeats(guildId).toList()
            newFeats.size shouldBe feats.size
            newFeats.first { it.name == featToEdit.name }.let {
                it.source shouldBe featToEdit.source
                it.link shouldBe featToEdit.link
            }
        }

        "Should not be able of updating the feats for a non-existent guild" {
            val feats = client.featsScope.getAllFeats(guildId).toList()
            shouldThrow<InvalidGuildException> {
                client.featsScope.rewriteAllFeats(
                    uuid(),
                    feats
                )
            }
        }

    }
}