package org.funql.ri.kotlinutil

/**
 * Created by hmf on 17.11.13.
 */

fun Array<out Any?>?.indexOf(value: Any?): Int {
    if (this == null)
        return -1;
    for (i in 0..this.size - 1) if (this[i] == value) return i
    return -1;
}

