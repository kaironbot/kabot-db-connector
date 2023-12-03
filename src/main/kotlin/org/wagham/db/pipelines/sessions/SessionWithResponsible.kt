package org.wagham.db.pipelines.sessions

import com.mongodb.client.model.UnwindOptions
import org.bson.conversions.Bson
import org.litote.kmongo.*
import org.wagham.db.models.Session

object SessionWithResponsible {

    fun getPipeline(skip: Int?, limit: Int?): List<Bson> =
        listOfNotNull(
            sort(descending(Session::date)),
            skip?.let { skip(it) },
            limit?.let { limit(it) },
            lookup(
                from = "players",
                localField = "registeredBy",
                foreignField = "_id",
                newAs = "registeredBy"
            ),
            unwind("\$registeredBy", UnwindOptions()
                .includeArrayIndex("unwindCounter")
                .preserveNullAndEmptyArrays(true))
        )

}
