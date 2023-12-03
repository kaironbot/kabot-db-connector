package org.wagham.db.scopes

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
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
                setOf(LabelType.CHARACTER.takeIf { Random.nextBoolean() } ?: LabelType.SESSION)
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
            client.labelsScope.createOrUpdateLabel(guildId, Label(uuid(), uuid(), setOf(LabelType.CHARACTER))) shouldBe true
            client.labelsScope.createOrUpdateLabel(guildId, Label(uuid(), uuid(), setOf(LabelType.SESSION))) shouldBe true

            val labelType = LabelType.CHARACTER.takeIf { Random.nextBoolean() } ?: LabelType.SESSION
            client.labelsScope.getLabels(guildId, labelType).onEach {
                it.types shouldContain labelType
            }.count() shouldBeGreaterThan 0
        }

        "Can retrieve labels by ids an type" {
            val label1 = Label(uuid(), uuid(), setOf(LabelType.CHARACTER))
            val label2 =  Label(uuid(), uuid(), setOf(LabelType.SESSION))
            client.labelsScope.createOrUpdateLabel(guildId, label1) shouldBe true
            client.labelsScope.createOrUpdateLabel(guildId, label2) shouldBe true

            client.labelsScope.getLabels(guildId, listOf(label1.id, label2.id)).let {
                it.toList() shouldContainExactlyInAnyOrder listOf(label1, label2)
            }

            client.labelsScope.getLabels(guildId, listOf(label1.id, label2.id), labelType = LabelType.SESSION).let {
                it.toList() shouldContainExactlyInAnyOrder listOf(label2)
            }

            client.labelsScope.getLabels(guildId, listOf(label1.id, label2.id), labelType = LabelType.ITEM).count() shouldBe 0
        }

        "Can retrieve labels by name" {
            val label = Label(uuid(), uuid(), setOf(LabelType.CHARACTER))
            client.labelsScope.createOrUpdateLabel(guildId, label) shouldBe true

            client.labelsScope.getLabelsByName(guildId, listOf(label.name, uuid())).toList() shouldContainExactlyInAnyOrder listOf(label)

        }

    }

}