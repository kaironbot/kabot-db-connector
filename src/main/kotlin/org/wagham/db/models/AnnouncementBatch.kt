package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

data class AnnouncementBatch(
    @BsonId val id: String,
    @JsonProperty("CriticalFail") val criticalFail: List<Announcement>,
    @JsonProperty("Fail") val fail: List<Announcement>,
    @JsonProperty("Success") val success: List<Announcement>,
    @JsonProperty("Jackpot") val jackpot: List<Announcement>,
    @JsonProperty("LostBeast") val lostBeast: List<Announcement>,
    @JsonProperty("WinBeast") val winBeast: List<Announcement>,
) {

    fun getAnnouncement(type: AnnouncementType): Announcement =
        when(type) {
            AnnouncementType.CriticalFail -> criticalFail.random()
            AnnouncementType.Fail -> fail.random()
            AnnouncementType.Success -> success.random()
            AnnouncementType.Jackpot -> jackpot.random()
            AnnouncementType.WinBeast -> winBeast.random()
            AnnouncementType.LostBeast -> lostBeast.random()
        }

}