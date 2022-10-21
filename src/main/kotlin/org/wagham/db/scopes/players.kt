package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.Player

fun KabotMultiDBClient.getAllPlayers(guildId: String) =
    this.getGuildDb(guildId)?.getCollection<Player>("players")?.find("{}")?.toFlow() ?: throw InvalidGuildException(guildId)
