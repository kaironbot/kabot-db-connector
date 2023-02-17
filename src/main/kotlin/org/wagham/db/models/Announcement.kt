package org.wagham.db.models

data class Announcement(
    val rawString: String
) {

    fun format(map: Map<String, String>) =
        map.entries.fold(rawString) { newString, it ->
            newString.replace(it.key, it.value)
        }

}