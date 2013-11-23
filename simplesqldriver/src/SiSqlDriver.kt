package org.funql.ri.sisql

import org.funql.ri.data.FunqlDriver
import java.util
import org.funql.ri.data.FunqlConnection

/**
 */

public class SiSqlDriver: FunqlDriver
{

    public override fun openConnection(name: String?, props: Map<String, String>?): FunqlConnection? = SiSqlConnectionDirect(name!!, props)

    override fun supportsRanges() = false

    override fun isAdvancedDriver() = false

}
