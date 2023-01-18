package org.wagham.db.models

import org.wagham.db.models.utils.Purchasable

data class Proficiency(
    override val name: String,
    override val isPurchasable: Boolean
) : Purchasable