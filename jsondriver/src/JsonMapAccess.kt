package org.funql.ri.jsondriver

import org.funql.ri.data.FqlDataException
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.data.FqlMultiMapContainer
import org.funql.ri.kotlinutil.KNamedImpl

class JsonMapAccess(name: String, val data: Map<*, *>): KNamedImpl(name), FqlMapContainer
{
    public override fun lookup(p0: Any?): Any? = data.get(p0)
}
