package org.wagham.db

import io.kotest.core.spec.style.StringSpec
import org.wagham.db.models.MongoCredentials
import org.wagham.db.scopes.*
import testBounties

class KabotMultiDBClientTest : StringSpec() {

    private val client = KabotMultiDBClient(
        MongoCredentials(
            "ADMIN",
            System.getenv("DB_TEST_USER")!!,
            System.getenv("DB_TEST_PWD")!!,
            System.getenv("TEST_DB")!!,
            System.getenv("DB_TEST_IP")!!,
            System.getenv("DB_TEST_PORT")!!.toInt(),
        )
    )
    private val guildId = System.getenv("TEST_DB_ID")!!

    init {
        testBackgrounds(client, guildId)
        testBounties(client, guildId)
        testBuildings(client, guildId)
        testCharacters(client, guildId)
        testFeats(client, guildId)
        testItems(client, guildId)
        testPlayers(client, guildId)
        testSpells(client, guildId)
        testSubclasses(client, guildId)
        testUtility(client, guildId)
    }
}
