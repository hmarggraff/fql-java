package org.funql.ri.jsondriver

import org.funql.ri.data.FunqlDriver
import java.util
import org.funql.ri.data.FunqlConnection

/**
 */

class JsonDriver: FunqlDriver
{

    //public override fun openConnection(name: String?, props: Map<String, String>?): FunqlConnection? = JsonConnection(name!!, props)
    public override fun openConnection(p0: String?, p1: Map<String, String>?): FunqlConnection? = JsonConnection(p0!!, p1)

    override fun supportsRanges() = true

    override fun isAdvancedDriver() = false

}
