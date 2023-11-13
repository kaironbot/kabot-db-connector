package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.NyxConfig
import org.wagham.db.models.ServerConfig
import org.wagham.db.utils.isSuccessful

class KabotDBServerConfigScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<ServerConfig> {

    override val collectionName = CollectionNames.SERVER_CONFIG.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<ServerConfig> =
        client.getGuildDb(guildId).getCollection(collectionName)

    /**
     * Retrieves the [ServerConfig] for the guild passed as parameter.
     *
     * @param guildId the id of the guild for which to get the config
     * @return a [ServerConfig]
     * @throws ResourceNotFoundException if no config was defined
     */
    suspend fun getGuildConfig(guildId: String): ServerConfig =
        getMainCollection(guildId)
            .findOne(ServerConfig::id eq "serverConfig")
            ?: throw ResourceNotFoundException("ServerConfig", "serverConfig")

    /**
     * Updates the current [ServerConfig] for a guild or creates it if it does not exist.
     *
     * @param guildId the id of the guild.
     * @param config the updated [ServerConfig].
     * @return true if the operation was successful, false otherwise
     */
    suspend fun setGuildConfig(guildId: String, config: ServerConfig) =
        getMainCollection(guildId).updateOne(
            ServerConfig::id eq "serverConfig",
            config.copy(id = "serverConfig"),
            UpdateOptions().upsert(true)
        ).isSuccessful()

    /**
     * Retrieves the [NyxConfig] for the guild passed as parameter.
     *
     * @param guildId the id of the guild.
     * @return a [NyxConfig]
     * @throws ResourceNotFoundException if no config was defined
     */
    suspend fun getNyxConfig(guildId: String): NyxConfig =
        client.getGuildDb(guildId).getCollection<NyxConfig>(collectionName)
            .findOne(NyxConfig::id eq "nyxConfig")
            ?: throw ResourceNotFoundException("ServerConfig", "nyxConfig")

    /**
     * Updates the current [NyxConfig] for a guild or creates it if it does not exist.
     *
     * @param guildId the id of the guild.
     * @param config the updated [NyxConfig].
     * @return true if the operation was successful, false otherwise
     */
    suspend fun setNyxConfig(guildId: String, config: NyxConfig): Boolean =
        client.getGuildDb(guildId).getCollection<NyxConfig>(collectionName)
            .updateOne(
                NyxConfig::id eq "nyxConfig",
                config.copy(id = "nyxConfig"),
                UpdateOptions().upsert(true)
            ).isSuccessful()

}