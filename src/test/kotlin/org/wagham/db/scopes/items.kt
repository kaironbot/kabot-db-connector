package org.wagham.db.scopes

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.onEach
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.models.Item

fun KabotMultiDBClientTest.testItems(
    client: KabotMultiDBClient,
    guildId: String
) {

    "Should be able to deserialize an item" {
        val exampleJson = "{\n" +
                "  \"_id\": \"1DayT1Badge\",\n" +
                "  \"sell_price\": 0,\n" +
                "  \"sell_proficiencies\": [\n" +
                "    \"ProficiencyMoneyBadge\"\n" +
                "  ],\n" +
                "  \"sell_building_requirement\": null,\n" +
                "  \"buy_price\": 0,\n" +
                "  \"is_usable\": false,\n" +
                "  \"link\": \"\",\n" +
                "  \"category\": \"TBadge\",\n" +
                "  \"manual\": \"ByWagham\",\n" +
                "  \"attunement\": false,\n" +
                "  \"give_ratio\": 0.5,\n" +
                "  \"buy_rep_requirement\": null,\n" +
                "  \"craft\": {}\n" +
                "}"
        val objectMapper = ObjectMapper()
            .registerKotlinModule()
        objectMapper.readValue(exampleJson, object : TypeReference<Item>(){})
    }

    "getAllItems should be able to get all the items" {
        val items = client.itemsScope.getAllItems(guildId).count() shouldBeGreaterThan 0
    }

}