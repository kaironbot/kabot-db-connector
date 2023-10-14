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

class KabotDBSpellScopeTest : StringSpec() {

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
        testSpells()
    }

    private fun StringSpec.testSpells() {

        "getAllSpells should be able to get all the spells" {
            val spells = client.spellsScope.getAllSpells(guildId)
            spells.count() shouldBeGreaterThan 0
        }

        "Should not be able of getting spells from a non-existent guild" {
            shouldThrow<InvalidGuildException> {
                client.spellsScope.getAllSpells(uuid())
            }
        }

        "Should be able of rewriting the whole spells collection" {
            val spells = client.spellsScope.getAllSpells(guildId).toList()
            val spellToEdit = spells.random().copy(
                school = uuid(),
                link = uuid()
            )
            client.spellsScope.rewriteAllSpells(
                guildId,
                spells.filter { it.name != spellToEdit.name } + spellToEdit
            ) shouldBe true
            val newSpells = client.spellsScope.getAllSpells(guildId).toList()
            newSpells.size shouldBe spells.size
            newSpells.first { it.name == spellToEdit.name }.let {
                it.school shouldBe spellToEdit.school
                it.link shouldBe spellToEdit.link
            }
        }

        "Should not be able of updating the spells for a non-existent guild" {
            val spells = client.spellsScope.getAllSpells(guildId).toList()
            shouldThrow<InvalidGuildException> {
                client.spellsScope.rewriteAllSpells(
                    uuid(),
                    spells
                )
            }
        }

    }
}