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
import org.funql.ri.data.NamedValues
import org.funql.ri.kotlinutil.namedValuesKImplSingle
import org.funql.ri.sisql.InsertStatementBuilder

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


    override fun put(fieldNames: Array<out String>?, value: Array<out Any>?): NamedValues? {
        /**
         * cases:
         * - insert
         *   - primary key is supplied
         *   - primary key is generated
         * - update
         *   - when primary key is supplied and exists
         */
        val pkIx: Int = fieldNames!!.indexOf(primaryKey)
        val statement = conn.createStatement()!!
        if (pkIx > 0) {
            // could be update
            val resultSet = statement.executeQuery("select count(*) from $table where $primaryKey = ${value!![pkIx]}")
            resultSet.next()
            val count: Int = resultSet.getInt(1)
            if (count == 1) {
                // update
                val sql: StringBuilder = StringBuilder("update $table set ")
                for (i in (0..fieldNames.size - 1)) {
                    if (i > 0)
                        sql.append(',')
                    if (value[i] is String)
                        sql.append("${fieldNames[i]}='${value[i]}'")
                    else
                        sql.append("${fieldNames[i]}=${value[i]}")
                }

                val sqlText = sql.toString()
                val cnt = statement.executeUpdate(sqlText)
                return namedValuesKImplSingle(fieldNames[pkIx], value[pkIx])
            } else if (count != 0)
                throw RuntimeException("Internal Error: Primary key can not have a count of: " + count)
        }
        val ins = InsertStatementBuilder(table)
        for (fix in fieldNames.indices) {
            ins.add(fieldNames[fix], value!![fix])
        }
        ins.addBatch(conn)
        val pks = ins.executeBatch()
        if (pks == null) {
            if (pkIx >= 0)
                return namedValuesKImplSingle(fieldNames[pkIx], value!![pkIx])
            else
                return null
        }
        else
            return namedValuesKImplSingle(fieldNames[pkIx], pks)
    }

    override fun put(fieldNames: Array<out String>?, value: Array<out Any>?, key: Any?): Unit {
        throw ConfigurationError("Sql does not support external keys.")
    }
    override fun commit() {
    }


}
