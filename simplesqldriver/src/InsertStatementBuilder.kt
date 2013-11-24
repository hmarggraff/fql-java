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

public class InsertStatementBuilder(val table: String)
{
    val values: ArrayList<Any?> = ArrayList<Any?>()
    val fields: ArrayList<String> = ArrayList<String>()
    var statement: PreparedStatement? = null

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

    fun addBatch(conn: Connection) {
        if (statement == null) {
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
            statement = conn.prepareStatement(b.toString(), Statement.RETURN_GENERATED_KEYS)!!
        }
        putField(statement!!, values)
        try {
            val res = statement!!.addBatch()
        } catch (x: Throwable) {
            x.printStackTrace()
            throw x
        }
        values.clear()
    }

    fun putField(st: PreparedStatement, vals: List<Any?>) {
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
                    putField(st, it.values.toList())
                }
                else -> throw IllegalArgumentException("it: " + it.javaClass)
            }
            cnt++
        }
    }

    fun executeBatch(): Any? {

        statement!!.executeBatch()
        val rs = statement!!.getGeneratedKeys()!!
        if (rs.next()) {
            val resultSetMetaData = rs.getMetaData()
            val cc = resultSetMetaData.getColumnCount()
            val res = rs.getObject(1)!!
            return res
        }
        return null;
    }
}