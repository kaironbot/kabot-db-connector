package org.wagham.db.pipelines.buildings

import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.client.model.UnwindOptions
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.conversions.Bson
import org.litote.kmongo.lookup
import org.litote.kmongo.match
import org.litote.kmongo.project
import org.litote.kmongo.unwind
import org.wagham.db.models.BaseBuilding
import org.wagham.db.models.Bounty

data class BuildingWithBounty (
    @BsonId override val name: String,
    override val type: String,
    override val tier: String,
    @JsonProperty("mo_cost") override val moCost: Int,
    override val materials: Map<String, Int> = emptyMap(),
    override val upgradeId: String? = null,
    override val upgradeOnly: Boolean = false,
    @JsonProperty("proficiency_reduction") override val proficiencyReduction: String?,
    @JsonProperty("bounty_id") override val bountyId: String,
    override val size: String,
    override val areas: List<String> = listOf(),
    @JsonProperty("desc_size") override val maxDescriptionSize: Int = 300,
    val bounty: Bounty,
) : BaseBuilding {
    companion object {

        fun getPipeline(): List<Bson> {
            return listOf(lookup("bounties", "bounty_id", "_id", "bounty"),
                    unwind("\$bounty", UnwindOptions()
                        .includeArrayIndex("unwindCounter")
                        .preserveNullAndEmptyArrays(false)),
                    match(Document(mapOf("unwindCounter" to 0))),
                    project(Document(mapOf("unwindCounter" to 0))))

        }

    }
}
