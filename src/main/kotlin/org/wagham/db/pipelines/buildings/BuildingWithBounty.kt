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
import org.wagham.db.models.Bounty

data class BuildingWithBounty (
    @BsonId val name: String,
    val type: String,
    val tier: String,
    @JsonProperty("mo_cost") val moCost: Int,
    val materials: Map<String, Int> = emptyMap(),
    val upgradeId: String? = null,
    val upgradeOnly: Boolean = false,
    @JsonProperty("proficiency_reduction") val proficiencyReduction: String?,
    @JsonProperty("bounty_id") val bountyId: String,
    val size: String,
    val areas: List<String> = listOf(),
    @JsonProperty("desc_size") val maxDescriptionSize: Int = 300,
    val bounty: Bounty,
) {
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
