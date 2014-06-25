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

public open class SiSqlConnection(name: String, propsArg: Map<String, String>?) : KFunqlConnection(name)
{
    val props: Map<String, String> = propsArg!!

    fun open(): Connection {
        println("Sisql connection opened")

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

    public override fun kgetIterator(streamName: String): FqlIterator {
        val resultSet = connection.createStatement()!!.executeQuery("select * from " + streamName)
        return SiSqlArrayIterator(streamName, resultSet)
    }


    override fun kgetUpdater(targetName: String, fieldNames: Array<out String>): Updater = SisqlUpdater(targetName, connection, fieldNames)

    public override fun kuseMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer {
        if (fieldpath.size() != 1)
            throw FqlDataException("The key for a SiSql map must be a single string, not a path: " + fieldpath.makeString("."))
        val keyField = fieldpath.get(0)
        val statement = connection.prepareStatement("select * from " + name + " where ? = " + keyField)!!

        return SiSqlMapAccess(name, statement)
    }


    override fun krange(name: String, startKey: String, endKey: String, includeEnd: Boolean): FqlIterator {
        throw UnsupportedOperationException()
    }
    public override fun close()
    {
        connection.close()
    }
    public override fun kgetMember(from: Any?, member: String): Any? = if (from is ResultSet) from.getObject(member) else null

    public override fun nextSequenceValue(sequenceName: String?): Any? {
        if (connection.javaClass.getName() == "org.hsqldb.jdbc.JDBCConnection")
        {
            val rs = connection.createStatement()!!.executeQuery("call NEXT VALUE FOR " + sequenceName)
            return if (rs.next()) rs.getLong(1) else throw FqlDataException("Cannot retrieve value from sequence: " + sequenceName)
        }
        else
            return 1L
    }

}

