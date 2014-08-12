package org.funql.ri.driver.json

import org.funql.ri.kotlinutil.KNamedImpl
import org.funql.ri.data.FqlMapContainer

class JsonMapAccess(name: String, val data: Map<*, *>): KNamedImpl(name), FqlMapContainer
{
    public override fun lookup(p0: Any?): Any? = data.get(p0)
}