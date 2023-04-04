package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import java.util.Date

data class Player(
    @BsonId val playerId: String,
    val name: String,
    val dateJoined: Date?,
    val isTraitor: Boolean,
    val linkedPlayers: Set<String> = emptySet()
)