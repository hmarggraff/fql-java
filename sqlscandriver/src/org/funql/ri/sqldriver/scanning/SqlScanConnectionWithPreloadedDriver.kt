package org.funql.ri.sqldriver.scanning

import org.funql.ri.data.FunqlConnection
import org.funql.ri.util.NameableImpl
import java.sql.Connection
import org.funql.ri.data.FqlIterator
import org.funql.ri.exec.Updater
import org.funql.ri.data.FqlMapContainer
import java.sql.ResultSet
import org.funql.ri.util.ConfigurationError
import org.funql.ri.sqldriver.SqlResultSetIterator
import org.funql.ri.sqldriver.SqlUpdater
import org.funql.ri.data.FqlDataException
import java.sql.DriverManager
import org.funql.ri.util.Keys
import org.funql.ri.sqldriver.SqlMapAccess

public open class SqlScanConnectionWithPreloadedDriver(name: String, propsArg: Map<String, String>?) : FunqlConnection, NameableImpl(name)
{
    val props: Map<String, String> = propsArg!!

    fun open(): Connection {
        println("SisqlDirect connection opened")
        val connectionStr: String? = props[Keys.connection]
        val userStr: String? = props[Keys.user]
        val passwdStr: String? = props[Keys.passwd]
        val driver_classStr = props[Keys.klass]
        if (connectionStr != null && userStr != null && passwdStr != null && driver_classStr != null)
        {
            Class.forName(driver_classStr)
            return DriverManager.getConnection(connectionStr, userStr, passwdStr)
        }
        else
            throw ConfigurationError("Simple Sql driver needs properties: connection, user, password, driver_class")
    }


    val connection = open()


    override fun getIterator(streamName: String): FqlIterator {
        val resultSet = connection.createStatement()!!.executeQuery("select * from " + streamName)
        return SqlResultSetIterator(streamName, resultSet)
    }



    override fun getUpdater(targetName: String, fieldNames: Array<out String>): Updater = SqlUpdater(targetName!!, connection, fieldNames!!)

    public override fun useMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer {
        if (fieldpath.size() != 1)
            throw FqlDataException("The key for a SiSql map must be a single string, not a path: " + fieldpath.makeString("."))
        val keyField = fieldpath[0]
        val statement = connection.prepareStatement("select * from " + name + " where ? = " + keyField)!!

        return SqlMapAccess(name, statement)
    }

    public override fun close()
    {
        connection.close()
    }
    public override fun getMember(from: Any, member: String): Any? = if (from is ResultSet) from.getObject(member) else null

    public override fun nextSequenceValue(sequenceName: String): Any {
        if (connection.javaClass.getName() == "org.hsqldb.jdbc.JDBCConnection")
        {
            val rs = connection.createStatement()!!.executeQuery("call NEXT VALUE FOR " + sequenceName)
            return if (rs.next()) rs.getLong(1) else throw org.funql.ri.data.FqlDataException("Cannot retrieve value from sequence: " + sequenceName)
        }
        else
            return 1L
    }


}