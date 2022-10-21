package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.Background

fun KabotMultiDBClient.getAllBackgrounds(guildId: String) =
    this.getGuildDb(guildId)?.getCollection<Background>("feats")?.find("{}")?.toFlow() ?: throw InvalidGuildException(guildId)