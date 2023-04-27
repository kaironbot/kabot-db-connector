package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId

data class ServerConfig(
    @BsonId val id: String = "",
    val adminRoleId: String? = null,
    val channels: Map<String, String> = emptyMap(),
    val eventChannels: Map<String, List<String>> = emptyMap(),
    val commandsPermissions: Map<String, List<String>> = emptyMap()
)

