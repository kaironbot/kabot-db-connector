package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.NyxRoles
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.MongoCredentials
import org.wagham.db.models.NyxConfig
import org.wagham.db.models.ServerConfig
import org.wagham.db.models.embed.EventConfig
import org.wagham.db.uuid

class KabotDBServerConfigScopeTest : StringSpec() {

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
        testServerConfigs()
    }

    private fun StringSpec.testServerConfigs() {

        val testChannelId = "868027091164229653"
        val testChannelIdKey = "TEST_CHANNEL_ID"
        val testCommand = "test_command"
        val testChannels = EventConfig(true, setOf("1234"))

        "Should be able to set a config and retrieve it" {
            val config = ServerConfig(
                id = "serverConfig",
                adminRoleId = uuid(),
                channels = mapOf(testChannelIdKey to testChannelId),
                eventChannels = mapOf(testCommand to testChannels)
            )
            client.serverConfigScope.setGuildConfig(guildId, config) shouldBe true
            client.serverConfigScope.getGuildConfig(guildId) shouldBe config
        }

        "Should not be able to get a server config for a non-existing Guild" {
            shouldThrow<InvalidGuildException> {
                client.serverConfigScope.getGuildConfig("I_DO_NOT_EXIST")
            }
        }

        "Should be able to set a nyx Config and retrieve it" {
            val config = NyxConfig(
                id = "nyxConfig",
                roleConfig = mapOf(uuid() to NyxRoles.MANAGE_SESSIONS)
            )
            client.serverConfigScope.setNyxConfig(guildId, config) shouldBe true
            client.serverConfigScope.getNyxConfig(guildId) shouldBe config
        }

    }
}