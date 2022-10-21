package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.Feat

fun KabotMultiDBClient.getAllFeats(guildId: String) =
    this.getGuildDb(guildId)?.getCollection<Feat>("feats")?.find("{}")?.toFlow() ?: throw InvalidGuildException(guildId)