package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.creation.CharacterCreationData
import org.wagham.db.uuid
import kotlin.random.Random

fun KabotMultiDBClientTest.testPlayers(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllPlayers should be able to get all the players" {
        val players = client.playersScope.getAllPlayers(guildId)
        players.count() shouldBeGreaterThan 0
    }

    "Cannot get players from a non existent guild" {
        shouldThrow<InvalidGuildException> {
            client.playersScope.getAllPlayers("I_DO_NOT_EXIST")
        }
    }

    "Should be able of setting and unsetting the active character" {
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

        client.playersScope.getPlayer(guildId, playerId).shouldNotBeNull().let {
            it.activeCharacter shouldBe "$playerId:${activeCharacter.name}"
        }

        client.playersScope.unsetActiveCharacter(guildId, playerId)

        client.playersScope.getPlayer(guildId, playerId).shouldNotBeNull().activeCharacter.shouldBeNull()
    }

}