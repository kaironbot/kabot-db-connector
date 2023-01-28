package org.wagham.db.scopes

import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.ServerConfig

class KabotDBServerConfigScope(
    private val client: KabotMultiDBClient
) {

    suspend fun getGuildConfig(guildId: String) =
        client.getGuildDb(guildId)
            .getCollection<ServerConfig>("serverConfig")
            .findOne(ServerConfig::id eq "serverConfig")
            ?: throw ResourceNotFoundException("ServerConfig", "serverConfig")

    suspend fun setGuildConfig(guildId: String, config: ServerConfig) =
        client.getGuildDb(guildId)
            .getCollection<ServerConfig>("serverConfig")
            .let {
                it.findOneAndReplace(ServerConfig::id eq "serverConfig", config.copy(id = "serverConfig"))
                    ?: it.insertOne(config.copy(id = "serverConfig"))
            }

}