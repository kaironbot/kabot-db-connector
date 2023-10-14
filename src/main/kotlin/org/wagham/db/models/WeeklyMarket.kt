package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.models.embed.CraftRequirement
import org.wagham.db.utils.ItemId
import org.wagham.db.utils.PlayerId
import java.util.Date

data class WeeklyMarket(
    @BsonId val date: Date,
    val items: Map<ItemId, CraftRequirement> = emptyMap(),
    val buyLog: Map<PlayerId, List<ItemId>> = emptyMap()
)