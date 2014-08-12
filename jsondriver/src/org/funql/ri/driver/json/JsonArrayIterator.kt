package org.funql.ri.driver.json

import org.funql.ri.kotlinutil.KNamedImpl
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.NamedValues
import org.funql.ri.util.NamedValuesImpl

public class JsonArrayIterator(name: String, val data: List<Any?>) : KNamedImpl(name), FqlIterator
{
    var pos = -1
    protected val names: Array<String> = array<String>("it")

    override fun next(): NamedValues? {
        if (pos >= data.size() - 1)
            return FqlIterator.sentinel
        pos = pos + 1;
        [suppress("CAST_NEVER_SUCCEEDS")]
        return NamedValuesImpl(names, array(data.get(pos)) as Array<Any>)
    }
}