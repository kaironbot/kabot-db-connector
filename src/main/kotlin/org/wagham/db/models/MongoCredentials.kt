package org.wagham.db.models

data class MongoCredentials (
    val username: String,
    val password: String,
    val database: String,
    val ip: String = "127.0.0.1",
    val port: Int = 27017,
)