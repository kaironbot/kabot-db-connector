package org.wagham.db.scopes

import com.mongodb.internal.connection.Server
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.ServerConfig

class KabotDBServerConfigScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<ServerConfig> {

    override val collectionName = "serverConfig"

    override fun getMainCollection(guildId: String): CoroutineCollection<ServerConfig> =
        client.getGuildDb(guildId).getCollection(collectionName)

    suspend fun getGuildConfig(guildId: String) =
        getMainCollection(guildId)
            .findOne(ServerConfig::id eq "serverConfig")
            ?: throw ResourceNotFoundException("ServerConfig", "serverConfig")

    suspend fun setGuildConfig(guildId: String, config: ServerConfig) =
        getMainCollection(guildId)
            .let {
                it.findOneAndReplace(ServerConfig::id eq "serverConfig", config.copy(id = "serverConfig"))
                    ?: it.insertOne(config.copy(id = "serverConfig"))
            }

}