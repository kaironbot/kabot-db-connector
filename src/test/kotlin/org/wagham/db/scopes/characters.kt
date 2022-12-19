package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.exceptions.NoActiveCharacterException

fun KabotMultiDBClientTest.testCharacters(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllCharacters should be able of getting all the characters" {
        client.charactersScope.getAllCharacters(guildId).count() shouldBeGreaterThan 0
    }

    "getActiveCharacter should be able to get the active character for a player" {
        val anActiveCharacter = client.charactersScope.getAllCharacters(guildId).firstOrNull{ it.status == CharacterStatus.active}
        anActiveCharacter shouldNotBe null
        val activeCharacter = client.charactersScope.getActiveCharacter(guildId, anActiveCharacter!!.player)
        activeCharacter shouldNotBe null
        activeCharacter.name shouldBe anActiveCharacter.name
        activeCharacter.player shouldBe anActiveCharacter.player
        activeCharacter.race shouldBe anActiveCharacter.race
        activeCharacter.territory shouldBe anActiveCharacter.territory
        activeCharacter.status shouldBe CharacterStatus.active
    }

    "getActiveCharacter should not be able to get data for a non existing player" {
        shouldThrow<NoActiveCharacterException> {
            client.charactersScope.getActiveCharacter(guildId, "I_DO_NOT_EXIST")
        }
    }

    "getCharactersWithPlayer should be able to get data for all the characters if no parameter is passed" {
        client.charactersScope.getCharactersWithPlayer(guildId).count() shouldBeGreaterThan 0
    }

    "getCharactersWithPlayer should be able to get data for all the characters with a parameter" {
        client.charactersScope.getCharactersWithPlayer(guildId, CharacterStatus.active)
            .onEach {
                it.status shouldBe CharacterStatus.active
            }
            .count() shouldBeGreaterThan 0
    }

}