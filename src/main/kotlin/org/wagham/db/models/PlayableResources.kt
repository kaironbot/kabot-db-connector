package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId

data class PlayableResources(
    @BsonId val docId: String = "playableResources",
    val classes: List<String> = emptyList(),
    val races: List<String> = emptyList()
)