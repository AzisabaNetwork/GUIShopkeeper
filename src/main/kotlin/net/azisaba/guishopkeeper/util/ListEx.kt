package net.azisaba.guishopkeeper.util

fun <E> List<E>.lastIndexOf(filter: (E) -> Boolean): Int {
    for (i in size - 1 downTo 0) {
        if (filter(this[i])) {
            return i
        }
    }
    return -1
}
