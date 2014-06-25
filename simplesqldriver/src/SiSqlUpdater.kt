package org.funql.ri.sisql

/**
 * Created by hmf on 16.11.13.
 */
/**
 * an updater for MongoDB
 */

import java.sql.Connection
import org.funql.ri.util.ConfigurationError
import org.funql.ri.data.NamedValues
import org.funql.ri.kotlinutil.KUpdater
import org.funql.ri.data.FqlDataException
import org.funql.ri.util.NamedValuesImpl

class SisqlUpdater(val table: String, val conn: Connection, fieldNames: Array<out String>) : KUpdater(fieldNames) {

    val primaryKey: String = {
        val databaseMetaData = conn.getMetaData()!!
        val primaryKeys = databaseMetaData.getPrimaryKeys(null, null, table.toUpperCase())!!
        var ret: String? = null



        while (primaryKeys.next()) {
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

    [suppress("CAST_NEVER_SUCCEEDS")]
    fun namedValuesSingleton(name: String, value: Any?) = NamedValuesImpl(array(name), array<Any?>(value) as Array<Any>)

    override fun kput(values: Array<out Any?>): NamedValues {
        /**
         * cases:
         * - insert
         *   - primary key is supplied
         *   - primary key is generated
         * - update
         *   - when primary key is supplied and exists
         */
        val pkIx: Int = fieldNames.indexOf(primaryKey)
        val statement = conn.createStatement()!!
        if (pkIx >= 0) {
            // could be update
            val resultSet = statement.executeQuery("select count(*) from $table where $primaryKey = ${values[pkIx]}")
            resultSet.next()
            val count: Int = resultSet.getInt(1)
            if (count == 1) {
                // update
                val sql: StringBuilder = StringBuilder("update $table set ")
                for (i in (0..fieldNames.size - 1)) {
                    if (i > 0)
                        sql.append(',')
                    if (values[i] is String)
                        sql.append("${fieldNames[i]}='${values[i]}'")
                    else
                        sql.append("${fieldNames[i]}=${values[i]}")
                }

                val sqlText = sql.toString()
                val cnt = statement.executeUpdate(sqlText)
                return namedValuesSingleton(fieldNames[pkIx], values[pkIx])
            } else if (count != 0)
                throw RuntimeException("Internal Error: Primary key can not have a count of: " + count)
            // else fall through to insert
        }
        val ins = InsertStatementBuilder(table, conn)
        for (fix in fieldNames.indices) {
            ins.add(fieldNames[fix], values[fix])
        }
        ins.addBatch()
        val pks = ins.executeBatch()
        if (pks == null) {
            if (pkIx >= 0)
                return namedValuesSingleton(fieldNames[pkIx], values[pkIx])
            else
                throw FqlDataException("put statement could not retrieve keys.")
        } else
            return namedValuesSingleton(primaryKey, pks)
    }


    override fun kput(values: Array<out Any?>, key: Any) {
        throw ConfigurationError("Sql does not support external keys.")
    }
    override fun commit() {
    }


}
