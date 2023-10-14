package org.wagham.db.utils

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.wagham.db.enums.CollectionNames

fun List<Int>.findPrevious(el: Float): Int {
    if (this.size == 1) return this[0]
    val middle = this[this.size / 2]
    return if (middle > el) this.subList(0, this.size / 2).findPrevious(el)
    else this.subList(this.size / 2, this.size).findPrevious(el)
}

inline fun <reified TDocument : Any> CoroutineDatabase.getCollection(collection: CollectionNames) =
    this.getCollection<TDocument>(collection.stringValue)