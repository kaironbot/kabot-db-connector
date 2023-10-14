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

class KabotDBProficiencyScopeTest : StringSpec() {

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
        testProficiencies()
    }

    private fun StringSpec.testProficiencies() {

        "Should be able of getting all the languages" {
            client.proficiencyScope.getLanguages(guildId).count() shouldBeGreaterThan 0
        }

        "Should not be able of getting the languages from a non-existing guild" {
            shouldThrow<InvalidGuildException> {
                client.proficiencyScope.getLanguages(uuid())
            }
        }

        "Should be able of rewriting the whole languages collection" {
            val languages = client.proficiencyScope.getLanguages(guildId).toList()
            val languageToEdit = languages.random().copy(name = uuid())
            client.proficiencyScope.rewriteAllLanguages(
                guildId,
                languages.filter { it.id != languageToEdit.id } + languageToEdit
            ) shouldBe true
            val newLanguages = client.proficiencyScope.getLanguages(guildId).toList()
            newLanguages.size shouldBe languages.size
            newLanguages.first { it.id == languageToEdit.id }.name shouldBe languageToEdit.name
        }

        "Should be able of getting all the tool proficiencies" {
            client.proficiencyScope.getToolProficiencies(guildId).count() shouldBeGreaterThan 0
        }

        "Should not be able of getting the tool proficiencies from a non-existing guild" {
            shouldThrow<InvalidGuildException> {
                client.proficiencyScope.getToolProficiencies(uuid())
            }
        }

        "Should be able of rewriting the whole tool proficiencies collection" {
            val tools = client.proficiencyScope.getToolProficiencies(guildId).toList()
            val toolToEdit = tools.random().copy(name = uuid())
            client.proficiencyScope.rewriteAllToolProficiencies(
                guildId,
                tools.filter { it.id != toolToEdit.id } + toolToEdit
            ) shouldBe true
            val newTools = client.proficiencyScope.getToolProficiencies(guildId).toList()
            newTools.size shouldBe tools.size
            newTools.first { it.id == toolToEdit.id }.name shouldBe toolToEdit.name
        }

    }
}