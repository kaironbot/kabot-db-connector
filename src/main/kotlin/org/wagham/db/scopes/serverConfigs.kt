package org.wagham.db.scopes

import org.bson.Document
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.ServerConfig

class KabotDBServerConfigScope(
    private val client: KabotMultiDBClient
) {

    companion object {
        val configSelector = Document(mapOf("_id" to "serverConfig"))
    }
    suspend fun getGuildConfig(guildId: String) =
        client.getGuildDb(guildId)
            ?.getCollection<ServerConfig>("serverConfig")
            ?.findOne(configSelector) ?: throw InvalidGuildException(guildId)

    suspend fun setGuildConfig(guildId: String, config: ServerConfig) =
        (client.getGuildDb(guildId) ?: throw InvalidGuildException(guildId))
            .getCollection<ServerConfig>("serverConfig")
            .let {
                it.findOneAndReplace(configSelector, config.copy(id = "serverConfig"))
                    ?: it.insertOne(config.copy(id = "serverConfig"))
            }


}