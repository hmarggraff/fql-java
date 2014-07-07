package org.funql.ri.kotlinutil.mapextensions
/**
 * Created by hans_m on 04.07.2014.
 */

import java.util.HashMap

public fun toStringMap(from: MutableMap<String, Any>?): MutableMap<String, String>? {
    if (from != null) {
        val ret = HashMap<String, String>()
        from.entrySet().forEach {
            val value = it.value
            ret.put(it.getKey(), value.toString())
        }
        return ret
    }
    else return null

}

