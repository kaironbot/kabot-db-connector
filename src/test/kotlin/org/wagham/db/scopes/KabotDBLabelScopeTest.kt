package org.wagham.db.scopes

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.onEach
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.LabelType
import org.wagham.db.models.MongoCredentials
import org.wagham.db.models.embed.Label
import org.wagham.db.uuid
import kotlin.random.Random

class KabotDBLabelScopeTest : StringSpec() {

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
        testLabels()
    }

    private fun StringSpec.testLabels() {

        "Can create a new label, retrieve it and update it" {
            val label = Label(
                uuid(),
                uuid(),
                LabelType.CHARACTER.takeIf { Random.nextBoolean() } ?: LabelType.SESSION
            )
            client.labelsScope.createOrUpdateLabel(
                guildId,
                label
            ) shouldBe true

            client.labelsScope.getLabel(guildId, label.id) shouldBe label

            val updatedLabel = label.copy(description = uuid())
            client.labelsScope.createOrUpdateLabel(guildId, updatedLabel) shouldBe true
            client.labelsScope.getLabel(guildId, label.id) shouldBe updatedLabel
        }

        "Can retrieve labels by type" {
            client.labelsScope.createOrUpdateLabel(guildId, Label(uuid(), uuid(), LabelType.CHARACTER)) shouldBe true
            client.labelsScope.createOrUpdateLabel(guildId, Label(uuid(), uuid(), LabelType.SESSION)) shouldBe true

            val labelType = LabelType.CHARACTER.takeIf { Random.nextBoolean() } ?: LabelType.SESSION
            client.labelsScope.getLabels(guildId, labelType).onEach {
                it.type shouldBe labelType
            }.count() shouldBeGreaterThan 0
        }

    }

}