package org.wagham.db

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.exceptions.NoActiveCharacterException
import org.wagham.db.models.MongoCredentials

class KabotMultiDBClientTest : StringSpec({

    val client = KabotMultiDBClient(
        mapOf(
            System.getenv("TEST_DB_ID")!! to MongoCredentials(
                System.getenv("DB_TEST_USER")!!,
                System.getenv("DB_TEST_PWD")!!,
                System.getenv("TEST_DB")!!,
                System.getenv("DB_TEST_IP")!!,
                System.getenv("DB_TEST_PORT")!!.toInt(),
            )
        )
    )

    "getActiveCharacter should be able to get the correct data for a Character" {
        val playerId = "617"
        val char = client.getActiveCharacter(
            System.getenv("TEST_DB_ID")!!,
            playerId
        )
        char shouldNotBe null
        char.name shouldBe "Wilhelm Thormar"
        char.player shouldBe playerId
        char.race shouldBe "Human"
        char.territory shouldBe "Gran Ducato di Vatakia"
        char.status shouldBe CharacterStatus.active
    }

    "getActiveCharacter should not be able to get data from a non existing Character" {
        shouldThrow<NoActiveCharacterException> {
            client.getActiveCharacter(
            System.getenv("TEST_DB_ID")!!,
    "I_DO_NOT_EXIST")
        }
    }

    "getActiveCharacter should not be able to get data from a retired Character" {
        shouldThrow<NoActiveCharacterException> {
            client.getActiveCharacter(
                System.getenv("TEST_DB_ID")!!,
                "test_retired")
        }
    }

    "getActiveCharacter should not be able to get data from a dead Character" {
        shouldThrow<NoActiveCharacterException> {
            client.getActiveCharacter(
                System.getenv("TEST_DB_ID")!!,
                "test_dead")
        }
    }

    "getActiveCharacter should not be able to get data from a npc Character" {
        shouldThrow<NoActiveCharacterException> {
            client.getActiveCharacter(
                System.getenv("TEST_DB_ID")!!,
                "test_npc")
        }
    }

    "getActiveCharacter should not be able to get data from a traitor Character" {
        shouldThrow<NoActiveCharacterException> {
            client.getActiveCharacter(
                System.getenv("TEST_DB_ID")!!,
                "test_npc")
        }
    }

})