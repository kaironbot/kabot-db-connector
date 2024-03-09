package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.models.embed.BuySellRequirement
import org.wagham.db.models.embed.CraftRequirement
import org.wagham.db.models.embed.LabelStub
import org.wagham.db.utils.JacksonLenientCraftDeserializer

@Serializable
data class Item (
    @BsonId val name: String,
    val sell: BuySellRequirement? = null,
    val buy: BuySellRequirement? = null,
    val usable: Boolean = false,
    val link: String? = null,
    val tier: String? = null,
    val category: String? = null,
    val manual: String? = null,
    val attunement: Boolean = false,
    val giveRatio: Float = 1f,
    @JsonDeserialize(using = JacksonLenientCraftDeserializer::class)
    val craft: List<CraftRequirement> = emptyList(),
    val labels: Set<LabelStub> = emptySet(),
    @JsonProperty("normalized_name") val normalizedName: String = ""
)