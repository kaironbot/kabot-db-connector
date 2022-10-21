package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.Spell

fun KabotMultiDBClient.getAllSpells(guildId: String) =
    this.getGuildDb(guildId)?.getCollection<Spell>("spells")?.find("{}")?.toFlow() ?: throw InvalidGuildException(guildId)