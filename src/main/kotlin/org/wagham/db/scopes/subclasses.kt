package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.Subclass

fun KabotMultiDBClient.getAllSubclasses(guildId: String) =
    this.getGuildDb(guildId)?.getCollection<Subclass>("classes")?.find("{}")?.toFlow() ?: throw InvalidGuildException(guildId)
