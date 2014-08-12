package org.funql.ri.sqldriver

import java.sql.Connection
import java.sql.Driver
import java.util.Properties
import java.util.UUID
import org.apache.logging.log4j.LogManager
import org.funql.ri.kotlinutil.KFunqlConnection
import org.funql.ri.exec.Updater
import org.funql.ri.data.FqlIterator
import org.funql.ri.util.ConfigurationError
import org.funql.ri.util.Keys
import org.funql.ri.classloading.JarClassLoader

public abstract class SqlConnectionBase(val props: Map<String, String>) : KFunqlConnection(props[Keys.name]!!)
{
    val configLog = LogManager.getLogger("config")!!

    fun open(): Connection {
        val connectionStr: String? = props[Keys.connection]
        val userStr: String? = props[Keys.user]
        val passwdStr: String? = props[Keys.passwd]
        val driver_classStr = props[Keys.klass]
        val jar = props[Keys.file]
        configLog.info("Sisql connection open(conn=$connectionStr,user=$userStr,class=$driver_classStr,pwdlen=${passwdStr?.length})")
        if (connectionStr != null && userStr != null && passwdStr != null && driver_classStr != null && jar != null)
        {
            val clazz: Class<out Any?> = JarClassLoader.loadClassFromJar(driver_classStr, jar)
            val driver = clazz.newInstance() as Driver
            val p = Properties()
            p.put(Keys.user, userStr)
            p.put(Keys.passwd, passwdStr)
            val connection = driver.connect(connectionStr, p)!!
            return connection
        }
        else
            throw ConfigurationError("Simple Sql driver needs properties: connection, user, password, driver_class, jar")
    }


    val connection = open()

    override fun getUpdater(targetName: String, fieldNames: Array<out String>): Updater = SqlUpdater(targetName, connection, fieldNames)


    public override fun close() = connection.close()

    public override fun nextSequenceValue(sequenceName: String): Any {
        if (connection.javaClass.getName() == "org.hsqldb.jdbc.JDBCConnection")
        {
            val rs = connection.createStatement()!!.executeQuery("call NEXT VALUE FOR " + sequenceName)
            return if (rs.next()) rs.getLong(1) else throw org.funql.ri.data.FqlDataException("Cannot retrieve value from sequence: " + sequenceName)
        }
        else
            return UUID.randomUUID()
    }

}