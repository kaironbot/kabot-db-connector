package org.wagham.db.scopes.characters

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.floats.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.*
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.exceptions.TransactionAbortedException
import io.kotest.matchers.types.shouldBeInstanceOf
import org.wagham.db.models.creation.CharacterCreationData
import org.wagham.db.models.embed.ProficiencyStub
import org.wagham.db.uuid
import kotlin.random.Random

fun KabotMultiDBClientTest.testCharacters(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllCharacters should be able of getting all the characters" {
        client.charactersScope.getAllCharacters(guildId).count() shouldBeGreaterThan 0
    }

    "getAllCharacters should be able of getting all the characters with a certain status" {
        client.charactersScope.getAllCharacters(guildId, CharacterStatus.active)
            .onEach {
                it.status shouldBe CharacterStatus.active
            }.count() shouldBeGreaterThan 0
    }

    "getActiveCharacter should be able to get the active character for a player" {
        val anActiveCharacter = client.charactersScope.getAllCharacters(guildId).firstOrNull{ it.status == CharacterStatus.active}.shouldNotBeNull()
        val activeCharacter = client.charactersScope.getActiveCharacterOrAllActive(guildId, anActiveCharacter.player)
            .currentActive.shouldNotBeNull()
        activeCharacter shouldNotBe null
        activeCharacter.name shouldBe anActiveCharacter.name
        activeCharacter.player shouldBe anActiveCharacter.player
        activeCharacter.race shouldBe anActiveCharacter.race
        activeCharacter.territory shouldBe anActiveCharacter.territory
        activeCharacter.status shouldBe CharacterStatus.active
    }

    "Should be possible to update a character" {
        val anActiveCharacter = client.charactersScope
            .getAllCharacters(guildId)
            .filter{ it.status == CharacterStatus.active}
            .take(1000)
            .toList()
            .random()

        val newRace = uuid()
        val newClass = uuid()
        client.charactersScope.updateCharacter(
            guildId,
            anActiveCharacter.copy(
                race = newRace,
                characterClass = anActiveCharacter.characterClass + newClass
            )
        )
        client.charactersScope.getCharacter(guildId, anActiveCharacter.id).let {
            it.race shouldBe newRace
            it.characterClass shouldBe listOf(anActiveCharacter.characterClass, newClass)
        }
    }

    "ms function should be able to get the correct ms for a player" {
        val c = client.charactersScope.getAllCharacters(guildId).firstOrNull{ it.status == CharacterStatus.active}
        c shouldNotBe null
        c!!.ms() shouldBe listOf(
            c.sessionMS,
            c.errataMS,
            c.pbcMS,
            c.masterMS
        ).sum()
    }

    "getActiveCharacter should not be able to get data for a non existing player" {
        client.charactersScope.getActiveCharacters(guildId, "I_DO_NOT_EXIST").count() shouldBe 0
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

    "getCharacter should throw an exception when trying to get a non-existing character" {
        shouldThrow<ResourceNotFoundException> {
            client.charactersScope.getCharacter(guildId, "I_DO_NOT_EXIST")
        }
    }

    "getCharacter should be able to get an existing character" {
        val aRandomCharacter = client.charactersScope.getAllCharacters(guildId).take(100).toList().random()
        val fetchedCharacter = client.charactersScope.getCharacter(guildId, aRandomCharacter.id)
        aRandomCharacter.name shouldBe fetchedCharacter.name
        aRandomCharacter.characterClass shouldBe fetchedCharacter.characterClass
    }

    "subtractMoney should be able of subtracting money from a character" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.money > 0 }
        val result = client.transaction(guildId) {
            client.charactersScope.subtractMoney(it, guildId, character.id, character.money) shouldBe true
            true
        }
        result.committed shouldBe true
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
        updatedCharacter.money shouldBe 0
    }

    "addMoney should be able of add money from a character" {
        val character = client.charactersScope.getAllCharacters(guildId).take(1000).toList().random()
        val amount = Random.nextFloat() * 1000
        val result = client.transaction(guildId) {
            client.charactersScope.addMoney(it, guildId, character.id, amount) shouldBe true
            true
        }
        result.committed shouldBe true
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
        updatedCharacter.money shouldBe character.money + amount
    }

    "All modifications should be preserved in a session but discarded if false is returned" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.money > 0 }
        val result = client.transaction(guildId) {
            client.charactersScope.subtractMoney(it, guildId, character.id, character.money) shouldBe true
            client.charactersScope.getCharacter(it, guildId, character.id).money shouldBe 0
            false
        }
        result.committed shouldBe false
        result.exception.shouldBeInstanceOf<TransactionAbortedException>()
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
        updatedCharacter.money shouldBeGreaterThan 0f
    }

    "All modifications should be preserved in a session but discarded if an exception is thrown" {
        val character = client.charactersScope.getAllCharacters(guildId).first { it.money > 0 }
        val newProficiency = ProficiencyStub(uuid(), uuid())
        val result = client.transaction(guildId) {
            client.charactersScope.addProficiencyToCharacter(it, guildId, character.id, newProficiency) shouldBe true
            client.charactersScope.getCharacter(it, guildId, character.id).proficiencies shouldContain newProficiency
            throw IllegalArgumentException("I do not like it")
        }
        result.committed shouldBe false
        result.exception.shouldBeInstanceOf<IllegalArgumentException>()
        result.exception?.message shouldBe "I do not like it"
        val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
        updatedCharacter.proficiencies shouldNotContain newProficiency
    }

    "Should be able to create a Character for an existing player" {
        val playerId = uuid()
        client.transaction(guildId) {
            client.playersScope.createPlayer(it, guildId, playerId, playerId) != null
        }.committed shouldBe true
        val player = client.playersScope.getPlayer(guildId, playerId).shouldNotBeNull()
        val data = CharacterCreationData(
            name = uuid(),
            startingLevel = "1",
            race = uuid(),
            characterClass = uuid(),
            null,
            null
        )
       client.charactersScope.createCharacter(
            guildId,
            player.playerId,
            player.name,
            data
        ).committed shouldBe true

        val character = client.charactersScope.getCharacter(guildId, "${player.playerId}:${data.name}")
        val expTable = client.utilityScope.getExpTable(guildId)
        character.name shouldBe data.name
        character.race shouldBe data.race
        character.characterClass shouldBe data.characterClass?.let { listOf(it) }
        character.territory shouldBe null
        character.ms() shouldBe expTable.levelToExp(data.startingLevel)
    }

    "Should be able to create a Character for a non-existing player" {
        val playerId = uuid()
        val playerName = uuid()
        val data = CharacterCreationData(
            name = uuid(),
            startingLevel = "${Random.nextInt(2, 10)}",
            race = uuid(),
            characterClass = uuid(),
            null,
            null
        )
        client.charactersScope.createCharacter(
            guildId,
            playerId,
            playerName,
            data
        ).committed shouldBe true

        val player = client.playersScope.getPlayer(guildId, playerId).shouldNotBeNull()
        player.name shouldBe playerName

        val character = client.charactersScope.getCharacter(guildId, "${player.playerId}:${data.name}")
        val expTable = client.utilityScope.getExpTable(guildId)
        character.name shouldBe data.name
        character.race shouldBe data.race
        character.characterClass shouldBe data.characterClass?.let { listOf(it) }
        character.territory shouldBe null
        character.ms() shouldBe expTable.levelToExp(data.startingLevel)
    }

    "getActiveCharacter should return an empty flow if the player has no active character" {
        val playerId = uuid()
        client.transaction(guildId) {
            client.playersScope.createPlayer(it, guildId, playerId, playerId) != null
        }.committed shouldBe true
        client.charactersScope.getActiveCharacters(guildId, playerId).count() shouldBe 0
    }

    "getActiveCharacter should be able to return all the active characters for a player if no active is specified" {
        val playerId = uuid()
        val playerName = uuid()
        val size = 3
        val characters = List(size) {
            CharacterCreationData(
                name = uuid(),
                startingLevel = "${Random.nextInt(2, 10)}",
                race = uuid(),
                characterClass = uuid(),
                null,
                null
            )
        }.onEach { data ->
            client.charactersScope.createCharacter(
                guildId,
                playerId,
                playerName,
                data
            ).committed shouldBe true
        }

        client.charactersScope.getActiveCharacterOrAllActive(guildId, playerId).let {
            it.currentActive shouldBe null
            it.allActive.size shouldBeGreaterThan 0
            it.allActive
        }.map { character ->
            characters.first { it.name == character.name }.let { ccd ->
                character.race shouldBe ccd.race
                character.characterClass shouldBe ccd.characterClass?.let { listOf(it) }
                character.age shouldBe null
                character.territory shouldBe null
            }
        }.count() shouldBe size
    }

    "getActiveCharacter should return the active characters if specified on the player" {
        val playerId = uuid()
        val playerName = uuid()
        val size = 3
        val characters = List(size) {
            CharacterCreationData(
                name = uuid(),
                startingLevel = "${Random.nextInt(2, 10)}",
                race = uuid(),
                characterClass = uuid(),
                null,
                null
            )
        }.onEach { data ->
            client.charactersScope.createCharacter(
                guildId,
                playerId,
                playerName,
                data
            ).committed shouldBe true
        }
        val activeCharacter = characters.first()
        client.playersScope.setActiveCharacter(guildId, playerId, "$playerId:${activeCharacter.name}") shouldBe true

        client.charactersScope.getActiveCharacterOrAllActive(guildId, playerId).let {
            it.allActive.size shouldBe 0
            it.currentActive.shouldNotBeNull()
        }.let { character ->
            character.race shouldBe activeCharacter.race
            character.characterClass shouldBe activeCharacter.characterClass?.let { listOf(it) }
            character.age shouldBe null
            character.territory shouldBe null
        }
    }

}