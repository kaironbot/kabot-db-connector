package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.enums.BuildingRestrictionType
import org.wagham.db.models.embed.EventConfig

data class ServerConfig(
    @BsonId val id: String = "",
    val adminRoleId: String? = null,
    val channels: Map<String, String> = emptyMap(),
    val eventChannels: Map<String, EventConfig> = emptyMap(),
    val commandsPermissions: Map<String, Set<String>> = emptyMap(),
    val buildingRestrictions: Map<BuildingRestrictionType, Int?> = emptyMap(),
)