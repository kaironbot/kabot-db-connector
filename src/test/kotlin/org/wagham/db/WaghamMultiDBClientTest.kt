package org.wagham.db

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.models.MongoCredentials


class WaghamMultiDBClientTest : StringSpec({

    val client = WaghamMultiDBClient(
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

    "this is a dummy test" {
        true shouldBe true
    }

})