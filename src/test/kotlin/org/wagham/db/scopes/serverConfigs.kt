package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.ServerConfig
import org.wagham.db.models.embed.EventConfig

fun KabotMultiDBClientTest.testServerConfigs(
    client: KabotMultiDBClient,
    guildId: String
) {

    val testChannelId = "868027091164229653"
    val testChannelIdKey = "TEST_CHANNEL_ID"
    val testCommand = "test_command"
    val testChannels = EventConfig(true, setOf("1234"))

    "Should be able to set a config and retrieve it" {
        client.serverConfigScope.setGuildConfig(
            guildId,
            ServerConfig(
                id = "serverConfig",
                channels = mapOf(testChannelIdKey to testChannelId),
                eventChannels = mapOf(testCommand to testChannels)
            )
        )
        client.serverConfigScope.getGuildConfig(guildId)
            .let {
                it.channels shouldContainKey testChannelIdKey
                it.channels[testChannelIdKey] shouldBe testChannelId
                it.eventChannels shouldContainKey testCommand
                it.eventChannels[testCommand]!!.enabled shouldBe true
                it.eventChannels[testCommand]!!.allowedChannels.size shouldBe 1
                it.eventChannels[testCommand]!!.allowedChannels.first() shouldBe "1234"
            }
    }

    "Should not be able to get a server config for a non-existing Guild" {
        shouldThrow<InvalidGuildException> {
            client.serverConfigScope.getGuildConfig("I_DO_NOT_EXIST")
        }
    }

}