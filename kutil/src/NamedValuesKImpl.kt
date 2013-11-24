package org.funql.ri.kotlinutil

/**
 * Created by hmf on 24.11.13.
 */

import org.funql.ri.data.NamedValues

public open class NamedValuesKImpl(val names1: Array<String>, val values1: Array<Any?>) : NamedValues{
    public override fun getNames(): Array<String> {
        return names1
    }
    public override fun getValues(): Array<Any> {
        return values1 as Array<Any>
    }
}
public fun namedValues4Map(src: Map<String, Any?>): NamedValuesKImpl {
    val tnames = Array<String>(src.size(), { "" })
    val tvalues = arrayOfNulls<Any?>(src.size())
    var cnt = 0
    for (e in src.entrySet())
    {
        tnames[cnt] = e.getKey()
        tvalues[cnt] = e.getValue()
        cnt++
    }
    return NamedValuesKImpl(tnames, tvalues)
}

public fun namedValuesKImplSingle(name: String, value: Any?): NamedValuesKImpl = NamedValuesKImpl(array<String>(name), array<Any?>(value))
