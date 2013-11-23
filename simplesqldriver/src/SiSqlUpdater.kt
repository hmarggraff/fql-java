/**
 * Created by hmf on 16.11.13.
 */
/**
 * an updater for MongoDB
 */

import java.sql.Connection
import org.funql.ri.util.ConfigurationError
import org.funql.ri.kotlinutil.indexOf
import org.funql.ri.exec.Updater

class SisqlUpdater(val table: String, val conn: Connection) : Updater{
    val primaryKey: String = {
        val databaseMetaData = conn.getMetaData()!!
        val primaryKeys = databaseMetaData.getPrimaryKeys(null, null, table.toUpperCase())!!
        var ret: String? = null


        while (primaryKeys.next())
        {
            val pk: String? = primaryKeys.getString("COLUMN_NAME");
            if (pk == null)
                throw ConfigurationError("The Simple SQL driver could not determine the primary key of table " + table)
            else if (ret != null)
                throw ConfigurationError("The Simple SQL driver does not support composite primary keys as used in table " + table)
            else
                ret = pk
        }
        if (ret == null)
            throw ConfigurationError("The Simple SQL driver could not determine the primary key of table " + table)
        else
            ret!!
    }()


    override fun put(fieldNames: Array<out String>?, value: Array<out Any>?): Any? {
        val pkIx: Int = fieldNames!!.indexOf(primaryKey)?:throw ConfigurationError("Sql updater needs primary key ($primaryKey) in field list.")
        val sql: StringBuilder = StringBuilder("update $table set ")
        for (i in (0..fieldNames.size - 1)) {
            if (i > 0)
                sql.append(',')
            if (value!![i] is String)
                sql.append("${fieldNames[i]}='${value!![i]}'")
            else
                sql.append("${fieldNames[i]}=${value!![i]}")
        }

        val cnt = conn.createStatement()!!.executeUpdate(sql.toString())!!
        conn.commit()

        return value!![pkIx]
    }


    override fun put(fieldNames: Array<out String>?, value: Array<out Any>?, key: Any?): Any? {
        throw ConfigurationError("Sql does not support external keys.")
    }
    override fun commit() {
    }


}
