package org.funql.ri.sisql

import java.util.ArrayList
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import org.funql.ri.test.genericobject.Ref
import org.funql.ri.test.genericobject.Key
import org.funql.ri.test.genericobject.TestObject
import java.sql.Statement

/**
 * Created by hmf on 01.11.13.
 */

public class InsertStatementBuilder(val table: String,
                                    connection: Connection)
{
    val values: ArrayList<Any?> = ArrayList<Any?>()
    val fields: ArrayList<String> = ArrayList<String>()
    val statement: PreparedStatement = initStatement(connection)

    fun add(field: String, value: Any?) {
        if (value is TestObject) {
            for (i: Int in (0..value.values.size - 1)) {
                val any = value.values[i]
                val str = value.typ.fields[i].name
                add(str, any)
            }
        }
        else {
            fields add field
            values add value
        }
    }

    fun initStatement(connection: Connection) : PreparedStatement {
        val b: StringBuilder = StringBuilder("insert into $table values (")
        var comma: Boolean = false;
        for (i in fields) {
            if (comma)
                b.append(',')
            else
                comma = true

            b.append("?")
        }
        b.append(")")
        return connection.prepareStatement(b.toString(), Statement.RETURN_GENERATED_KEYS)!!
    }

    fun addBatch() {
        putFields(statement, values)
        try {
            statement.addBatch()
        } catch (x: Throwable) {
            x.printStackTrace()
            throw x
        }
        values.clear()
    }

    fun putFields(st: PreparedStatement, vals: List<Any?>) {
        var cnt: Int = 1
        vals.forEach {
            when (it) {
                is String -> st.setString(cnt, it)
                is Long -> st.setLong(cnt, it)
                is Int -> st.setInt(cnt, it)
                is Boolean -> st.setBoolean(cnt, it)
                is Double -> st.setDouble(cnt, it)
                is Date -> st.setDate(cnt, it)
                is Ref -> st.setLong(cnt, it.target.oid)
                is Key -> st.setLong(cnt, it.target.oid)
                is TestObject -> {
                    putFields(st, it.values.toList())
                }
                else -> throw IllegalArgumentException("it: " + it.javaClass)
            }
            cnt++
        }
    }

    fun executeBatch(): Any? {

        statement.executeBatch()
        val rs = statement.getGeneratedKeys()!!
        if (rs.next()) {
            /* val resultSetMetaData = */ rs.getMetaData()
            //val cc = resultSetMetaData.getColumnCount()
            val res = rs.getObject(1)!!
            return res
        }
        return null;
    }
}