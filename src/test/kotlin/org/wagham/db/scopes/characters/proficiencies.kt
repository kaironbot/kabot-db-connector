package org.wagham.db.scopes.characters

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.models.embed.ProficiencyStub
import org.wagham.db.uuid

fun KabotMultiDBClientTest.testCharactersProficiencies(
    client: KabotMultiDBClient,
    guildId: String
) {

    "Should be able to add and remove a proficiency to a Character" {
        val character = client.charactersScope.getAllCharacters(guildId).first{
            it.name.startsWith(('A'..'Z').random().toString())
        }
        val newProficiency = ProficiencyStub(uuid(), uuid())
        client.charactersScope.addProficiencyToCharacter(
            guildId,
            character.id,
            newProficiency
        ) shouldBe true

        client.charactersScope.getCharacter(guildId, character.id).proficiencies shouldContain newProficiency

        client.charactersScope.removeProficiencyFromCharacter(
            guildId,
            character.id,
            newProficiency
        ) shouldBe true

        client.charactersScope.getCharacter(guildId, character.id).proficiencies shouldNotContain newProficiency
    }

    "Adding a proficiency two times should result in a failure" {
        val character = client.charactersScope.getAllCharacters(guildId).first{
            it.name.startsWith(('A'..'Z').random().toString())
        }
        val newProficiency = ProficiencyStub(uuid(), uuid())
        client.charactersScope.addProficiencyToCharacter(
            guildId,
            character.id,
            newProficiency
        ) shouldBe true

        client.charactersScope.getCharacter(guildId, character.id).proficiencies shouldContain newProficiency

        client.charactersScope.addProficiencyToCharacter(
            guildId,
            character.id,
            newProficiency
        ) shouldBe false
    }

    "Removing a non-existent proficiency should result in a failure" {
        val character = client.charactersScope.getAllCharacters(guildId).first{
            it.name.startsWith(('A'..'Z').random().toString())
        }
        val newProficiency = ProficiencyStub(uuid(), uuid())
        client.charactersScope.removeProficiencyFromCharacter(
            guildId,
            character.id,
            newProficiency
        ) shouldBe false

        client.charactersScope.getCharacter(guildId, character.id).proficiencies shouldNotContain newProficiency
    }

    "Should be able to add and remove a language to a Character" {
        val character = client.charactersScope.getAllCharacters(guildId).first{
            it.name.startsWith(('A'..'Z').random().toString())
        }
        val newLanguage = ProficiencyStub(uuid(), uuid())
        client.transaction(guildId) {
            client.charactersScope.addLanguageToCharacter(
                it,
                guildId,
                character.id,
                newLanguage
            ) shouldBe true
            true
        }.committed shouldBe true

        client.charactersScope.getCharacter(guildId, character.id).languages shouldContain newLanguage

        client.transaction(guildId) {
            client.charactersScope.removeLanguageFromCharacter(
                it,
                guildId,
                character.id,
                newLanguage
            ) shouldBe true
            true
        }

        client.charactersScope.getCharacter(guildId, character.id).languages shouldNotContain newLanguage
    }

    "Adding a language two times should result in a failure" {
        val character = client.charactersScope.getAllCharacters(guildId).first{
            it.name.startsWith(('A'..'Z').random().toString())
        }
        val newLanguage = ProficiencyStub(uuid(), uuid())

        client.transaction(guildId) {
            client.charactersScope.addLanguageToCharacter(
                it,
                guildId,
                character.id,
                newLanguage
            ) shouldBe true
            true
        }.committed shouldBe true


        client.charactersScope.getCharacter(guildId, character.id).languages shouldContain newLanguage

        client.transaction(guildId) {
            client.charactersScope.addLanguageToCharacter(
                it,
                guildId,
                character.id,
                newLanguage
            ) shouldBe false
            false
        }.committed shouldBe false

    }

    "Removing a non-existent language should result in a failure" {
        val character = client.charactersScope.getAllCharacters(guildId).first{
            it.name.startsWith(('A'..'Z').random().toString())
        }
        val newLanguage = ProficiencyStub(uuid(), uuid())
        client.transaction(guildId) {
            client.charactersScope.removeLanguageFromCharacter(
                it,
                guildId,
                character.id,
                newLanguage
            ) shouldBe false
            false
        }.committed shouldBe false

        client.charactersScope.getCharacter(guildId, character.id).languages shouldNotContain newLanguage
    }
}