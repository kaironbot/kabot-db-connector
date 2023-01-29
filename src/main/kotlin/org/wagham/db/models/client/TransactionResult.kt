package org.wagham.db.models.client

data class TransactionResult(
    val committed: Boolean,
    val exception: Exception? = null
)