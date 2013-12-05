package org.funql.ri.test.util

import org.funql.ri.data.FqlIterator
import org.funql.ri.data.NamedValues

fun dump(s: Any?): String {
    val sb = StringBuilder()
    dump(s, sb, 0)
    return sb.toString()
}
fun dump(s: Any?, sb: StringBuilder, indent: Int) {
    if (s == FqlIterator.sentinel)
        sb.append("|")
    else if (s == null)
        sb.append("~")
    else if (s is Map<*, *>) {
        sb.append('{')
        var cnt = 0
        val entrySet: Set<Map.Entry<Any?, Any?>> = s.entrySet()
        for (e in entrySet) {
            if (cnt > 0) sb.append(',')
            cnt++
            sb.append(e.getKey()).append(':')
            dump(e.getValue(), sb, indent + 1)
        }
        sb.append("}")

    }
/*    else if (s is NamedValues) {
        sb.append('{')
        for (i in 0 .. s.getNames()!!.size-1){
            if (i > 0) sb.append(',')
            sb.append(s.getNames()!![i]).append(':')
            dump(s.getValues()!![i], sb, indent + 1)
        }
        sb.append("}")
    }
    */
    else if (s is FqlIterator) {
        sb.append('[');
        var cnt = 0
        while (true) {
            val el = s.next()
            if (el == FqlIterator.sentinel) break
            if (cnt > 0) sb.append(',')
            cnt++
            dump(el, sb, indent + 1)
        }
        sb.append("]")
    }
    else if (s is Number || s is Boolean) {
        sb.append(s.toString())
    }
    else {
        dumpString(s, sb)
    }

}

fun dumpString(s: Any?, sb: StringBuilder) {
    if (s == null) {
        sb.append('~')
        return
    }
    val s1 = s.toString()
    if (isIdentifier(s1))
        sb.append(s1)
    else
        sb.append('\'').append(s1).append('\'')

}


fun isIdentifier(s: String): Boolean {
    for (ch in s) {
        if (!ch.isJavaIdentifierPart())
            return false
    }
    return true
}

