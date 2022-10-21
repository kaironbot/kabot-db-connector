package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

data class Subclass (
    @BsonId val id: Int,
    @JsonProperty("class") val dndClass: String,
    val subclass: String,
    val race: List<String>,
    val territory: String,
    val link: String,
    val source: String
)