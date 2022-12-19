package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

data class Spell(
    @BsonId val name: String,
    val level: Int,
    val srd: Boolean,
    val school: String,
    @JsonProperty("class") val dndClass: List<String>,
    val ritual: Boolean,
    val link: String,
    val manual: String
)