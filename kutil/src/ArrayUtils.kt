package org.funql.ri.kotlinutil
/**
 * Created by hmf on 17.11.13.
 */

fun Array<out Any?>.indexOf(value: Any?): Int? {
    for (i in 0..this.size-1) if (this[i] == value) return i
    return null;
}

