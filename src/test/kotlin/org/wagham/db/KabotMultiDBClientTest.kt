package org.wagham.db

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.count
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.exceptions.NoActiveCharacterException
import org.wagham.db.models.MongoCredentials
import org.wagham.db.scopes.getActiveCharacter
import org.wagham.db.scopes.getItems

class KabotMultiDBClientTest : StringSpec({

    val client = KabotMultiDBClient(
             MongoCredentials("ADMIN",
                System.getenv("DB_TEST_USER")!!,
                System.getenv("DB_TEST_PWD")!!,
                System.getenv("TEST_DB")!!,
                System.getenv("DB_TEST_IP")!!,
                System.getenv("DB_TEST_PORT")!!.toInt(),
            )
    )
    val guildId = System.getenv("TEST_DB_ID")!!

    "getActiveCharacter should be able to get the correct data for a Character" {
        val playerId = "617"
        val char = client.getActiveCharacter(guildId, playerId)
        char shouldNotBe null
        char.name shouldBe "Wilhelm Thormar"
        char.player shouldBe playerId
        char.race shouldBe "Human"
        char.territory shouldBe "Gran Ducato di Vatakia"
        char.status shouldBe CharacterStatus.active
    }

    "getActiveCharacter should not be able to get data from a non existing Character" {
        shouldThrow<NoActiveCharacterException> {
            client.getActiveCharacter(guildId, "I_DO_NOT_EXIST")
        }
    }

    "getActiveCharacter should not be able to get data from a retired Character" {
        shouldThrow<NoActiveCharacterException> {
            client.getActiveCharacter(guildId,"test_retired")
        }
    }

    "getActiveCharacter should not be able to get data from a dead Character" {
        shouldThrow<NoActiveCharacterException> {
            client.getActiveCharacter(guildId,"test_dead")
        }
    }

    "getActiveCharacter should not be able to get data from a npc Character" {
        shouldThrow<NoActiveCharacterException> {
            client.getActiveCharacter(guildId, "test_npc")
        }
    }

    "getActiveCharacter should not be able to get data from a traitor Character" {
        shouldThrow<NoActiveCharacterException> {
            client.getActiveCharacter(
                System.getenv("TEST_DB_ID")!!,
                "test_npc")
        }
    }

    "getItems should be able to get all the items" {
        val items = client.getItems(guildId)
        items.count() shouldBe 10
    }

})