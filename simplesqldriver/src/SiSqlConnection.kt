package org.funql.ri.sisql

import org.funql.ri.data.FqlDataException
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.util.ConfigurationError
import java.sql.DriverManager
import java.sql.Connection
import java.sql.ResultSet
import org.funql.ri.exec.Updater
import org.funql.ri.kotlinutil.KFunqlConnection
import org.funql.ri.classloading.JarClassLoader
import org.funql.ri.util.Keys
import java.util.UUID
import org.apache.logging.log4j.LogManager
import java.util.Properties


//val configLog = LogManager.getLogger("config")!!

public open class SiSqlConnection(propsArg: Map<String, String>) : SqlConnectionBase(propsArg)
{
    public override fun kgetIterator(streamName: String): FqlIterator {
        val resultSet = connection.createStatement()!!.executeQuery("select * from " + streamName)
        return SqlResultSetIterator(streamName, resultSet)
    }


    override fun kgetUpdater(targetName: String, fieldNames: Array<out String>): Updater = SqlUpdater(targetName, connection, fieldNames)

    public override fun kuseMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer {
        if (fieldpath.size() != 1)
            throw FqlDataException("The key for a SiSql map must be a single string, not a path: " + fieldpath.makeString("."))
        val keyField = fieldpath.get(0)
        val statement = connection.prepareStatement("select * from " + name + " where ? = " + keyField)!!

        return SiSqlMapAccess(name, statement)
    }


    public override fun kgetMember(from: Any?, member: String): Any? = if (from is ResultSet) from.getObject(member) else null

}

