package org.funql.ri.kotlinutil

/**
 * Created by hmf on 01.11.13.
 */

public fun StringBuilder.invoke(a: Any?) {
       this.append(a)
    }

public fun joinList(strings: Array<String>, separator: Char, target:StringBuffer): StringBuffer {
    if (strings != null) {
        var continuation = false

        for (s in strings) {
            if (s == null) continue
            if (continuation)
                target.append(separator)
            else
                continuation = true
            target.append(s)
        }
    }
    return target
}
