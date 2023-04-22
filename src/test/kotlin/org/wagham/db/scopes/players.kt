package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.uuid

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

    "Can connect the accounts of two players" {
        val players = client.playersScope.getAllPlayers(guildId).take(1000).toList()
        val firstPlayer = players.random()
        val secondPlayer = players.filter { it.playerId != firstPlayer.playerId }.random()
        client.transaction(guildId) {
            client.playersScope.connectPlayers(it, guildId, firstPlayer.playerId, secondPlayer.playerId) shouldBe true
            true
        }.committed shouldBe true
        client.playersScope.getPlayer(guildId, firstPlayer.playerId).let {
            it shouldNotBe null
            it!!.linkedPlayers shouldContain secondPlayer.playerId
        }
        client.playersScope.getPlayer(guildId, secondPlayer.playerId).let {
            it shouldNotBe null
            it!!.linkedPlayers shouldContain firstPlayer.playerId
        }
    }

    "Cannot connect two accounts if one of the two does not exist" {
        val players = client.playersScope.getAllPlayers(guildId).take(1000).toList()
        val firstPlayer = players.random()
        val secondPlayer = players.filter { it.playerId != firstPlayer.playerId }.random()
        client.transaction(guildId) {
            client.playersScope.connectPlayers(it, guildId, firstPlayer.playerId, uuid()) shouldBe false
            client.playersScope.connectPlayers(it, guildId, uuid(), secondPlayer.playerId) shouldBe false
            false
        }.committed shouldBe false
        client.playersScope.getPlayer(guildId, firstPlayer.playerId).let {
            it shouldNotBe null
            it!!.linkedPlayers shouldNotContain secondPlayer.playerId
        }
        client.playersScope.getPlayer(guildId, secondPlayer.playerId).let {
            it shouldNotBe null
            it!!.linkedPlayers shouldNotContain firstPlayer.playerId
        }
    }

}