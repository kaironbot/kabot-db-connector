package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.uuid

fun KabotMultiDBClientTest.testSubclasses(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllSubclasses should be able to get all the subclasses" {
        val subclasses = client.subclassesScope.getAllSubclasses(guildId)
        subclasses.count() shouldBeGreaterThan 0
    }

    "Should not be able of getting subclasses from a non-existent guild" {
        shouldThrow<InvalidGuildException> {
            client.subclassesScope.getAllSubclasses(uuid())
        }
    }

    "Should be able of rewriting the whole subclass collection" {
        val classes = client.subclassesScope.getAllSubclasses(guildId).toList()
        val classToEdit = classes.random().copy(
            subclass = uuid(),
            link = uuid()
        )
        client.subclassesScope.rewriteAllSubclasses(
            guildId,
            classes.filter { it.id != classToEdit.id } + classToEdit
        ) shouldBe true
        val newClasses = client.subclassesScope.getAllSubclasses(guildId).toList()
        newClasses.size shouldBe classes.size
        newClasses.first { it.id == classToEdit.id }.let {
            it.subclass shouldBe classToEdit.subclass
            it.link shouldBe classToEdit.link
        }
    }

    "Should not be able of updating the subclasses for a non-existent guild" {
        val classes = client.subclassesScope.getAllSubclasses(guildId).toList()
        shouldThrow<InvalidGuildException> {
            client.subclassesScope.rewriteAllSubclasses(
                uuid(),
                classes
            )
        }
    }

}