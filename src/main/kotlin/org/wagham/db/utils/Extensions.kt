package org.wagham.db.utils

fun List<Int>.findPrevious(el: Float): Int {
    if (this.size == 1) return this[0]
    val middle = this[this.size / 2]
    if (middle > el) return this.subList(0, this.size / 2).findPrevious(el)
    else return this.subList(this.size / 2, this.size).findPrevious(el)
}