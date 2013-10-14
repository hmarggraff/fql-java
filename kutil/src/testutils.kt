package org.funql.ri.test.util

import org.funql.ri.exec.NamedLong
import org.funql.ri.exec.NamedDouble
import org.funql.ri.exec.NamedBoolean
import org.funql.ri.exec.NamedValue
import org.funql.ri.data.FqlIterator

fun dump(s: Any?): String {
    val sb = StringBuffer()
    dump(s, sb, 0)
    return sb.toString()
}
fun dump(s: Any?, sb: StringBuffer, indent: Int) {
    if (s == FqlIterator.sentinel)
        sb.append("|")
    else if (s == null)
        sb.append("~")
    else if (s is Map<*,*>) {
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
    else if (s is NamedLong || s is NamedDouble || s is NamedBoolean) {
        val s1 = (s as NamedValue)
        sb.append(s1.getVal())
    }
    else if (s is NamedValue) {
        dump(s.getVal(), sb, indent+1)
    }
    else if (s is Array<Any?>) {  // object returned by select statement
        if (s is Array<NamedValue?>)
            System.currentTimeMillis()
        sb.append('{');
        var cnt = 0
        s.forEach {
            if (cnt > 0) sb.append(',')
            cnt++
            sb.append((it as NamedValue).getName()).append(':')
            dump(it, sb, indent + 1)
        }
        sb.append("}")
    }
    else if (s is List<Any?>) {
        sb.append('[');
        var cnt = 0
        s.forEach {
            if (cnt > 0) sb.append(',')
            cnt++
            dump(it, sb, indent + 1)
        }
        sb.append("]")
    }
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
        val s1 = s.toString()
        if (isIdentifier(s1))
            sb.append(s1)
        else
            sb.append('\'').append(s1).append('\'')
    }
}

    fun isIdentifier(s:String):Boolean{
        for (ch in s) {
            if (!ch.isJavaIdentifierPart())
                return false
        }
        return true
    }

