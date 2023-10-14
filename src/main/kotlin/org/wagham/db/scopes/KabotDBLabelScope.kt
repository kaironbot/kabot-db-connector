package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.enums.LabelType
import org.wagham.db.models.embed.Label
import org.wagham.db.utils.isSuccessful

class KabotDBLabelScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Label> {

    override val collectionName = CollectionNames.LABELS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<Label> =
        client.getGuildDb(guildId).getCollection(collectionName)

    suspend fun createOrUpdateLabel(guildId: String, label: Label) =
        getMainCollection(guildId)
            .updateOne(
                Label::id eq label.id,
                label,
                UpdateOptions().upsert(true)
            ).isSuccessful()

    fun getLabels(guildId: String, labelType: LabelType? = null) =
        getMainCollection(guildId).find(labelType?.let { Label::type eq it }).toFlow()

    suspend fun getLabel(guildId: String, labelId: String) =
        getMainCollection(guildId).findOne(Label::id eq  labelId)
}