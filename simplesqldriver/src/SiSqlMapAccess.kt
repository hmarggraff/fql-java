package org.funql.ri.sisql

import org.funql.ri.data.FqlDataException
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.data.FqlMultiMapContainer
import java.sql.PreparedStatement
import org.funql.ri.kotlinutil.KNamedImpl

public class SiSqlMapAccess(name: String, val statement: PreparedStatement): KNamedImpl(name), FqlMapContainer
{
    public override fun lookup(key: Any?): Any? = {
        statement.setObject(1,key)
        statement.executeQuery()
    }
}
