package org.funql.ri.sqldriver.scanning

import org.funql.ri.sqldriver.SqlConnectionBase
import org.funql.ri.data.FqlIterator
import org.funql.ri.exec.Updater
import org.funql.ri.data.FqlMapContainer
import java.sql.ResultSet
import org.funql.ri.sqldriver.SqlResultSetIterator
import org.funql.ri.sqldriver.SqlUpdater
import org.funql.ri.data.FqlDataException
import org.funql.ri.sqldriver.SqlMapAccess

public open class SqlScanConnection(propsArg: Map<String, String>) : SqlConnectionBase(propsArg)
{
    public override fun getIterator(streamName: String): FqlIterator {
        val resultSet = connection.createStatement()!!.executeQuery("select * from " + streamName)
        return SqlResultSetIterator(streamName, resultSet)
    }


    override fun getUpdater(targetName: String, fieldNames: Array<out String>): Updater = SqlUpdater(targetName, connection, fieldNames)

    public override fun useMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer {
        if (fieldpath.size() != 1)
            throw FqlDataException("The key for a SiSql map must be a single string, not a path: " + fieldpath.makeString("."))
        val keyField = fieldpath[0]
        val statement = connection.prepareStatement("select * from " + name + " where ? = " + keyField)!!

        return SqlMapAccess(name, statement)
    }


    public override fun getMember(from: Any, member: String): Any? = if (from is ResultSet) from.getObject(member) else null

}