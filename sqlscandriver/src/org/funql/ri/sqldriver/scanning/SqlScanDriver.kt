package org.funql.ri.sqldriver.scanning

import org.funql.ri.data.FunqlDriver
import org.funql.ri.data.FunqlConnection

public class SqlScanDriver : FunqlDriver
{


    override fun openConnection(name: String, props: Map<String, String>?): FunqlConnection? = SqlScanConnectionWithPreloadedDriver(name, props)

    override fun supportsRanges() = false

    override fun isAdvancedDriver() = false

}