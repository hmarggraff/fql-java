package org.funql.ri.kotlinutil

import java.util.HashMap
import java.util.ArrayList

fun List<Any?>?.notEmpty(role: String) : Array<Any> {
    if (this == null) throw AssertionError("The $role list may not be null")
    val ret:  Array<Any> = Array<Any>(this.size(), {this[it]?:throw AssertionError("The $role list may not contain null elements")})
    return ret
}
fun List<String?>?.dotJoin(role: String) :String {
    if (this == null) throw AssertionError("The $role list may not be null")
    val ret:  StringBuffer = StringBuffer()
    var dot = false
    this.forEach{
        if (dot)
            ret.append('.')
        else
            dot = true
        ret.append(it!!)
    }
    return ret.toString()
}

public inline fun <K,V> javaHashMap(vararg values: Pair<K, V>): HashMap<K,V> {
    val answer = HashMap<K,V>(values.size)
    for (v in values) {
        answer.put(v.first, v.second)
    }
    return answer
}

fun Map<String?, String?>?.check(): Map<String, String?> {
    if (this == null) throw AssertionError("The checked map may not be null")
    val ret = HashMap<String, String?>()
    this.entrySet().forEach { ret.put(it.getKey()!!, it.getValue()) }
    return ret;
}

fun List<String?>?.check(): List<String> {
    if (this == null) throw AssertionError("The map may not be null")
    val ret = ArrayList<String>(this.size)
    this.forEach { if (it == null) throw AssertionError("The checked list may not contain null elements"); ret.add(it) }
    return ret;

}fun List<String?>?.check(name: String): List<String> {
    if (this == null) throw AssertionError("The list $name may not be null")
    val ret = ArrayList<String>()
    this.forEach { if (it == null) throw AssertionError("The list $name may not contain null elements"); ret.add(it) }
    return ret;
}

