package org.funql.ri.jsondriver

import org.funql.ri.data.FqlDataException
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.data.FqlMultiMapContainer

class JsonMultiMapAccess(name: String, val data: Map<*, *>): KNamedImpl(name), FqlMultiMapContainer
{
    public override fun lookup(p0: Any?): FqlIterator? {
        val ret = data[p0]
        if (ret is List<*>)
            return JsonArrayIterator(getName(), ret)
        throw FqlDataException("Entry of multi-map " + getName() + " is not a list. but a " + ret.javaClass)
    }
}
