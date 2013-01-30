package org.funql.ri.jsondriver

import org.funql.ri.data.FunqlDriver
import java.util
import org.funql.ri.data.FqlConnection

/**
 */

class JsonDriver: FunqlDriver
{
    public override fun openConnection(p0: String?, p1: Map<String?, String?>?): FqlConnection?  = JsonConnection(name = p0!!, propsArg = p1)

    override fun supportsRanges() = true

    override fun isAdvancedDriver() = false
}
