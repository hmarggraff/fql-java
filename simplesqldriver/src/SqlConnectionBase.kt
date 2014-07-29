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


public abstract class SqlConnectionBase(val props: Map<String, String>) : KFunqlConnection(props[Keys.name]!!)
{
    val configLog = LogManager.getLogger("config")!!

    fun open(): Connection {
        val connectionStr: String? = props.get(Keys.connection)
        val userStr: String? = props.get(Keys.user)
        val passwdStr: String? = props.get(Keys.passwd)
        val driver_classStr = props[Keys.klass]
        val jar = props[Keys.file]
        configLog.info("Sisql connection open(conn=$connectionStr,user=$userStr,class=$driver_classStr,pwdlen=${passwdStr?.length})")
        if (connectionStr != null && userStr != null && passwdStr != null && driver_classStr != null && jar != null)
        {
            val clazz: Class<out Any?> = JarClassLoader.loadClassFromJar(driver_classStr, jar)
            val driver = clazz.newInstance() as java.sql.Driver
            val p = Properties()
            p.put("user", userStr)
            p.put("password", passwdStr)
            val connection = driver.connect(connectionStr, p)!!
            return connection
        }
        else
            throw ConfigurationError("Simple Sql driver needs properties: connection, user, password, driver_class, jar")
    }


    val connection = open()

    override fun kgetUpdater(targetName: String, fieldNames: Array<out String>): Updater = SqlUpdater(targetName, connection, fieldNames)


    override fun krange(name: String, startKey: String, endKey: String, includeEnd: Boolean): FqlIterator {
        throw UnsupportedOperationException()
    }
    public override fun close() = connection.close()

    public override fun nextSequenceValue(sequenceName: String?): Any? {
        if (connection.javaClass.getName() == "org.hsqldb.jdbc.JDBCConnection")
        {
            val rs = connection.createStatement()!!.executeQuery("call NEXT VALUE FOR " + sequenceName)
            return if (rs.next()) rs.getLong(1) else throw FqlDataException("Cannot retrieve value from sequence: " + sequenceName)
        }
        else
            return UUID.randomUUID()
    }

}

