package org.funql.ri.sqldriver.mapping

import org.funql.ri.sqldriver.SqlConnectionBase
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import java.sql.ResultSet
import org.funql.ri.sqldriver.SqlResultSetIterator
import org.funql.ri.data.FqlDataException
import org.funql.ri.sqldriver.SqlMapAccess

class MappedSqlConnection(props: Map<String,String>): SqlConnectionBase(props) {

    public override fun getIterator(query: String): FqlIterator {
        val resultSet = connection.createStatement()!!.executeQuery(query)
        return SqlResultSetIterator("query", resultSet)
    }


    public override fun useMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer {
        if (fieldpath.size() != 1)
            throw FqlDataException("The key for a SiSql map must be a single string, not a path: " + fieldpath.joinToString("."))
        val keyField = fieldpath[0]
        val statement = connection.prepareStatement("select * from " + name + " where ? = " + keyField)!!

        return SqlMapAccess(name, statement)
    }


    public override fun getMember(from: Any, member: String): Any? = if (from is ResultSet) from.getObject(member) else null
}