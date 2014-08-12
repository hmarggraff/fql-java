package org.funql.ri.sqldriver

import java.sql.PreparedStatement
import org.funql.ri.kotlinutil.KNamedImpl
import org.funql.ri.data.FqlMapContainer

public class SqlMapAccess(name: String, val statement: PreparedStatement): KNamedImpl(name), FqlMapContainer
{
    public override fun lookup(key: Any?): Any? = {
        statement.setObject(1,key)
        statement.executeQuery()
    }
}