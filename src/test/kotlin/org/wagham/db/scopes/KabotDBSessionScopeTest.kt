package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.MongoCredentials
import org.wagham.db.models.dto.SessionOutcome
import org.wagham.db.models.embed.LabelStub
import org.wagham.db.utils.dateAtMidnight
import org.wagham.db.utils.daysInBetween
import org.wagham.db.uuid
import java.util.*
import kotlin.random.Random

class KabotDBSessionScopeTest : StringSpec() {

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
        testSessions()
    }

    private fun StringSpec.testSessions() {

        "getAllSessions should be able to get all the sessions" {
            client.sessionScope.getAllSessions(guildId).count() shouldBeGreaterThan 0
        }

        "getAllSessions should be able to get all the sessions after a certain date" {
            val startingDate = client.sessionScope.getAllSessions(guildId).take(1000).toList().random().date
            client.sessionScope
                .getAllSessions(guildId, startDate = startingDate)
                .onEach {
                    it.date shouldBeGreaterThanOrEqualTo startingDate
                }.count() shouldBeGreaterThan 0
        }

        "getAllSessions should be able to get all the sessions before a certain date" {
            val endDate = client.sessionScope.getAllSessions(guildId).take(1000).toList().random().date
            client.sessionScope
                .getAllSessions(guildId, endDate = endDate)
                .onEach {
                    it.date shouldBeLessThanOrEqualTo endDate
                }.count() shouldBeGreaterThan 0
        }

        "getAllSessions should be able to get all the sessions between two dates" {
            val startingDate = client.sessionScope.getAllSessions(guildId).take(1000).toList().random().date
            val endDate = client.sessionScope.getAllSessions(guildId).filter {
                it.date > startingDate
            }.take(1000).toList().random().date
            client.sessionScope
                .getAllSessions(guildId, startDate = startingDate, endDate = endDate)
                .onEach {
                    it.date shouldBeGreaterThanOrEqualTo startingDate
                    it.date shouldBeLessThanOrEqualTo endDate
                }.count() shouldBeGreaterThan 0
        }

        "Cannot get sessions from a non existent guild" {
            shouldThrow<InvalidGuildException> {
                client.sessionScope.getAllSessions("I_DO_NOT_EXIST")
            }
        }

        "Can get a session by UID" {
            val session = client.sessionScope.getAllSessions(guildId).take(100).toList().random()
            client.sessionScope.getSessionByUid(guildId, session.uid) shouldBe session
        }

        "Can get a session by Title" {
            val session = client.sessionScope.getAllSessions(guildId).take(100).toList().random()
            client.sessionScope.getSessionsByTitle(guildId, session.title).first() shouldBe session
        }

        "Can get all the sessions for a player" {
            val master = client.sessionScope.getAllSessions(guildId).take(1000).toList().random().master
            val masterPlayer = client.charactersScope.getCharacter(guildId, master).player
            client.sessionScope.getAllMasteredSessions(guildId, masterPlayer)
                .onEach {
                    client.charactersScope.getCharacter(guildId, it.master).player shouldBe masterPlayer
                    it.masterCharacter.player shouldBe masterPlayer
                }.count() shouldBeGreaterThan 0
        }

        "Can calculate the days passed in-game between two dates" {
            val calendar = Calendar.getInstance()
            val startDate = client.sessionScope.getAllSessions(guildId).take(1000).toList().random().date
            calendar.time = startDate
            calendar.add(Calendar.DAY_OF_WEEK, +7)
            val endDate = calendar.time

            val normalizedStartDate = dateAtMidnight(startDate)
            val normalizedEndDate = dateAtMidnight(endDate)
            val inGameDays = client.sessionScope
                .getAllSessions(guildId)
                .filter {
                    it.date in normalizedStartDate..normalizedEndDate
                }.toList()
                .groupBy { it.date }
                .map { sessions -> sessions.value.maxOf { it.duration } }
                .sum() + 1L + daysInBetween(normalizedStartDate, normalizedEndDate)

            client.sessionScope.getTimePassedInGame(guildId, startDate, endDate) shouldBe inGameDays
        }

        "Can register a new session and delete it" {
            val responsible = client.playersScope.getAllPlayers(guildId).toList().random()
            val (master, character, dead) = client.charactersScope.getAllCharacters(guildId, CharacterStatus.active)
                .toList()
                .shuffled()
                .take(3)

            val title = uuid()
            val characterOutcome = SessionOutcome(character.id, Random.nextInt(1, 6), false)
            val deadOutcome = SessionOutcome(dead.id, 0, true)
            val labels = setOf(LabelStub(uuid(), uuid()))
            val masterReward = 1
            val date = Date()

            val sessionId = uuid()
            client.sessionScope.insertSession(
                guildId = guildId,
                sessionId = sessionId,
                masterId = master.id,
                masterReward = masterReward,
                title = title,
                date = date,
                outcomes = listOf(characterOutcome, deadOutcome),
                labels = labels,
                registeredBy = responsible.playerId
            ).committed shouldBe true

            val session = client.sessionScope.getSessionById(guildId, sessionId).shouldNotBeNull()
            session.master shouldBe master.id
            client.charactersScope.getCharacter(guildId, master.id).let {
                it.masterMS shouldBe (master.masterMS + masterReward)
            }
            session.labels shouldBe labels
            session.characters.map { it.character } shouldContainExactlyInAnyOrder listOf(character.id, dead.id)
            session.characters.onEach {
                if(it.character == character.id) {
                    it.ms shouldBe characterOutcome.exp
                    it.isAlive shouldBe !characterOutcome.isDead
                } else {
                    it.ms shouldBe deadOutcome.exp
                    it.isAlive shouldBe !deadOutcome.isDead
                }
            }.size shouldBe 2

            val updatedCharacter = client.charactersScope.getCharacter(guildId, character.id)
            updatedCharacter.ms() shouldBe (character.ms() + characterOutcome.exp)
            updatedCharacter.status shouldBe character.status
            updatedCharacter.errata shouldBe character.errata

            val updatedDeadCharacter = client.charactersScope.getCharacter(guildId, dead.id)
            updatedDeadCharacter.ms() shouldBe dead.ms()
            updatedDeadCharacter.status shouldBe CharacterStatus.dead
            updatedDeadCharacter.errata.size shouldBe (dead.errata.size + 1)

            client.sessionScope.deleteSession(guildId, sessionId, masterReward).committed shouldBe true

            client.charactersScope.getCharacter(guildId, master.id).let {
                it.masterMS shouldBe master.masterMS
            }

            client.charactersScope.getCharacter(guildId, character.id).let {
                it.ms() shouldBe character.ms()
                it.status shouldBe character.status
                it.errata shouldContainExactlyInAnyOrder character.errata
            }
            client.charactersScope.getCharacter(guildId, dead.id).let {
                it.ms() shouldBe dead.ms()
                it.status shouldBe dead.status
                it.errata shouldContainExactlyInAnyOrder dead.errata
            }

        }

        "Can get all the sessions with the responsible player" {
            client.sessionScope.getSessionsWithResponsible(guildId).toList().shouldNotBeEmpty().any {
                it.registeredBy != null
            } shouldBe true
        }

    }
}