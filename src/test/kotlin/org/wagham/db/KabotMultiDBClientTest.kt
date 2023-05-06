package org.wagham.db

import io.kotest.core.spec.style.StringSpec
import org.wagham.db.models.MongoCredentials
import org.wagham.db.scopes.*
import org.wagham.db.scopes.characters.testCharacters
import org.wagham.db.scopes.characters.testCharactersInventories
import org.wagham.db.scopes.characters.testCharactersProficiencies
import org.wagham.db.scopes.utility.testAttendance
import org.wagham.db.scopes.utility.testExpTable
import org.wagham.db.scopes.utility.testUtility
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
        testAttendance(client, guildId)
        testBackgrounds(client, guildId)
        testBounties(client, guildId)
        testBuildings(client, guildId)
        testCharacters(client, guildId)
        testCharactersInventories(client, guildId)
        testCharactersProficiencies(client, guildId)
        testExpTable(client, guildId)
        testFeats(client, guildId)
        testFlame(client, guildId)
        testItems(client, guildId)
        testPlayers(client, guildId)
        testProficiencies(client, guildId)
        testRaces(client, guildId)
        testServerConfigs(client, guildId)
        testSessions(client, guildId)
        testSpells(client, guildId)
        testSubclasses(client, guildId)
        testUtility(client, guildId)
    }
}
