package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.enums.NyxRoles

data class NyxConfig(
    @BsonId val id: String = "nyxConfig",
    val roleConfig: Map<String, NyxRoles> = emptyMap()
)
