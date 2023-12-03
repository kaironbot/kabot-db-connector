package org.wagham.db.models

interface BaseBuilding {
    val name: String
    val type: String
    val tier: String
    val moCost: Int
    val materials: Map<String, Int>
    val upgradeId: String?
    val upgradeOnly: Boolean
    val proficiencyReduction: String?
    val size: String
    val areas: List<String>
    val maxDescriptionSize: Int
    val bountyId: String
}