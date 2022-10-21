package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.Item

fun KabotMultiDBClient.getAllItems(guildId: String) =
    this.getGuildDb(guildId)?.getCollection<Item>("items")?.find("{}")?.toFlow() ?: throw InvalidGuildException(guildId)
