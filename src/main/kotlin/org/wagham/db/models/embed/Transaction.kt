package org.wagham.db.models.embed

import org.wagham.db.enums.TransactionType
import java.util.Date

data class Transaction (
    val date: Date,
    val from: String,
    val type: TransactionType,
    val args: Map<String, Float> = emptyMap()
)
