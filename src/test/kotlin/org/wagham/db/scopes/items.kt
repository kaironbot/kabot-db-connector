package org.wagham.db.scopes

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.onEach
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.Item

fun KabotMultiDBClientTest.testItems(
    client: KabotMultiDBClient,
    guildId: String
) {
    
    "getAllItems should be able to get all the items" {
        client.itemsScope.getAllItems(guildId).count() shouldBeGreaterThan 0
    }

    "Cannot get items from a non existent guild" {
        shouldThrow<InvalidGuildException> {
            client.itemsScope.getAllItems("I_DO_NOT_EXIST")
        }
    }

}