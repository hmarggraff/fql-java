package org.funql.ri.sisql

import java.util.ArrayList
import java.util.LinkedHashMap
import org.funql.ri.data.FunqlConnectionWithRange
import org.funql.ri.data.FqlDataException
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.util.ConfigurationError
import org.funql.ri.util.ListFqlIterator
import java.sql.DriverManager
import java.sql.Connection
import org.funql.ri.data.FunqlConnection
import java.sql.ResultSet
import java.sql.PreparedStatement
import org.funql.ri.kotlinutil.KNamedImpl

public open class SiSqlConnection(name: String, propsArg: Map<String, String>?) : KNamedImpl(name), FunqlConnection
{
    val props: Map<String, String> = propsArg!!

    fun open(): Connection {
        val connectionStr: String? = props.get("connection")
        val userStr: String? = props.get("user")
        val passwdStr: String? = props.get("password")
        val driver_classStr = props.get("driver_class")
        if (connectionStr != null && userStr != null && passwdStr != null && driver_classStr != null)
        {
            Class.forName(driver_classStr)
            return DriverManager.getConnection(connectionStr, userStr, passwdStr)
        }
        else
            throw ConfigurationError("Simple Sql driver needs properties: connection, user, password, driver_class")
    }


    val connection = open()

    public override fun getIterator(streamName: String?): FqlIterator? {
        val tableName = streamName!!
        val resultSet = connection.createStatement()!!.executeQuery("select * from " + tableName)
        return SiSqlArrayIterator(streamName, resultSet)
    }

    public override fun useMap(name: String?, fieldpath: List<String>?, single: Boolean): FqlMapContainer? {
        val fields = fieldpath!!
        if (fields.size() != 1)
            throw FqlDataException("The key for a SiSql map must be a single string, not a path: " + fieldpath.makeString("."))
        val keyField = fields.get(0)
        val statement = connection.prepareStatement("select * from " + name + " where ? = " + keyField)!!

        return SiSqlMapAccess(name!!, statement)
    }

    public override fun close()
    {
        connection.close()
    }
    public override fun getMember(from: Any?, member: String?): Any? = if (from is ResultSet) from.getObject(member) else null
}

