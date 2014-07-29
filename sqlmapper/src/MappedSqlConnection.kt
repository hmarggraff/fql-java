package org.funql.ri.msql
/**
 * Created by hans_m on 29.07.2014.
 */

import org.funql.ri.kotlinutil.KFunqlConnection
import org.funql.ri.util.Keys
import java.sql.Connection
import org.funql.ri.classloading.JarClassLoader
import java.util.Properties
import org.funql.ri.util.ConfigurationError
import org.funql.ri.data.FqlIterator
import org.funql.ri.sisql.SqlResultSetIterator
import org.funql.ri.exec.Updater
import org.funql.ri.sisql.SqlUpdater
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.data.FqlDataException
import org.funql.ri.sisql.SiSqlMapAccess
import java.sql.ResultSet
import java.util.UUID
import org.funql.ri.sisql.SqlConnectionBase

class MappedSqlConnection(props: Map<String,String>): SqlConnectionBase(props) {

    public override fun kgetIterator(query: String): FqlIterator {
        val resultSet = connection.createStatement()!!.executeQuery(query)
        return SqlResultSetIterator("query", resultSet)
    }


    public override fun kuseMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer {
        if (fieldpath.size() != 1)
            throw FqlDataException("The key for a SiSql map must be a single string, not a path: " + fieldpath.makeString("."))
        val keyField = fieldpath.get(0)
        val statement = connection.prepareStatement("select * from " + name + " where ? = " + keyField)!!

        return SiSqlMapAccess(name, statement)
    }


    public override fun kgetMember(from: Any?, member: String): Any? = if (from is ResultSet) from.getObject(member) else null
}
