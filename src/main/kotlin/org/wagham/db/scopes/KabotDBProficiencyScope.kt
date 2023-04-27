package org.wagham.db.scopes

import org.bson.BsonDocument
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.LanguageProficiency
import org.wagham.db.models.ToolProficiency

class KabotDBProficiencyScope(
    private val client: KabotMultiDBClient
) {

    fun getLanguages(guildId: String) =
        client.getGuildDb(guildId)
            .getCollection<LanguageProficiency>("languages")
            .find(BsonDocument())
            .toFlow()

    suspend fun rewriteAllLanguages(guildId: String, languages: List<LanguageProficiency>) =
        client.getGuildDb(guildId)
            .getCollection<LanguageProficiency>("languages")
            .let {
                it.deleteMany(BsonDocument())
                it.insertMany(languages)
            }.insertedIds.size == languages.size

    fun getToolProficiencies(guildId: String) =
        client.getGuildDb(guildId)
            .getCollection<ToolProficiency>("tools")
            .find(BsonDocument())
            .toFlow()

    suspend fun rewriteAllToolProficiencies(guildId: String, proficiencies: List<ToolProficiency>) =
        client.getGuildDb(guildId)
            .getCollection<ToolProficiency>("tool")
            .let {
                it.deleteMany(BsonDocument())
                it.insertMany(proficiencies)
            }.insertedIds.size == proficiencies.size


}