package org.wagham.db.models.embed

import org.wagham.db.enums.TransactionType
import java.util.Date

data class Transaction (
    val date: Date,
    val otherParty: String?,
    val operation: String,
    val type: TransactionType,
    val args: Map<String, Float> = emptyMap()
)
