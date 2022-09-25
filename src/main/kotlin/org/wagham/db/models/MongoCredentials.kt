package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty

data class  MongoCredentials (
    @JsonProperty("_id") val guildId: String,
    val username: String,
    val password: String,
    val database: String,
    val ip: String = "127.0.0.1",
    val port: Int = 27017,
) {
    fun toConnectionString() =
        "mongodb://${username}:${password}@${ip}:${port}/${database}"
}