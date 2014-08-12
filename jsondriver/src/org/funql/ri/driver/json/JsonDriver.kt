package org.funql.ri.driver.json

import org.funql.ri.data.FunqlDriver
import org.funql.ri.data.FunqlConnection

class JsonDriver: FunqlDriver
{

    override fun openConnection(name: String, props: Map<String, String>?): FunqlConnection?  = JsonConnection(name, props)

    override fun supportsRanges() = true

    override fun isAdvancedDriver() = false

}