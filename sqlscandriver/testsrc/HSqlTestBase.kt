/*
 * Copyright (C) 2011, Hans Marggraff and other copyright owners as documented in the project's IP log.
 * This program and the accompanying materials are made available under the terms of the Eclipse Distribution License v1.0 which accompanies this distribution, is reproduced below, and is available at http://www.eclipse.org/org/documents/edl-v10.php
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the Eclipse Foundation, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.funql.ri.sisql.test

import java.util.HashMap
import org.funql.ri.data.FqlIterator
import java.sql.ResultSet
import org.funql.ri.test.cameradata.CameraData
import org.funql.ri.test.genericobject.Types
import java.sql.Connection
import org.funql.ri.test.genericobject.TypeDef
import org.funql.ri.kotlinutil.FqlMaterializedQuery
import org.funql.ri.test.genericobject.FieldDef
import org.funql.ri.test.genericobject.TestObject
import org.testng.Assert
import kotlin.test.fail
import org.funql.ri.test.util.dumpString
import java.util.ArrayList
import kotlin.jdbc.getValues
import org.funql.ri.sqldriver.InsertStatementBuilder
import org.funql.ri.sqldriver.scanning.SqlScanConnectionWithPreloadedDriver
import org.funql.ri.util.Keys


/**
 * Unit test for simple Sql Driver.
 */
open class HSqlTestBase
{
    val hsqlDriverClassName = "org.hsqldb.jdbc.JDBCDriver"
    val connectionStr = "jdbc:hsqldb:mem:testdb"
    protected val sisConn: SqlScanConnectionWithPreloadedDriver = {
        val p = HashMap<String, String>()
        p.put("driver", "org.funql.ri.sqldriver.scanning.SiSqlDriver")
        p.put("connection", connectionStr)
        p.put("user", "SA")
        p.put("password", "")
        p.put("driver_class", hsqlDriverClassName)
        SqlScanConnectionWithPreloadedDriver("name", p)
    }()

    val conn: Connection = sisConn.connection


    fun writeChecked(typ: TypeDef, data: Array<TestObject>, conn: Connection)
    {
        writeData(typ, data, conn)
        val rowCnt = count(typ.name, conn)
        Assert.assertEquals(rowCnt, data.size)
    }

    fun count(table: String, conn: Connection): Int {
        val res = conn.createStatement()!!.executeQuery("select count(*) from " + table)
        if (res.next())
            return res.getInt(1)
        fail("No rows in: " + CameraData.cameraFields.name)
        return -1 // to make the compiler happy


    }

    fun createTable(typ: TypeDef, conn: Connection) {
        val b = StringBuilder("create table ${typ.name} (\n  id BIGINT primary key")
        writeFieldDefs(typ.fields, b)
        b append "\n)\n"
        println(b)
        conn.createStatement()!!.executeQuery(b.toString())
    }

    fun writeFieldDefs(fields: Array<FieldDef>, b: StringBuilder, prefix: String? = null) {
        for (it in fields) {
            if (it.typ == Types.obj)
                writeFieldDefs(it.refType!!.fields, b, it.name)
            else if (it.typ != Types.array){
                b append ",\n  "
                if (prefix != null){
                    b append prefix
                    b append '_'
                }
                b append it.name
                b append ' '
                b.append(sqltype(it.typ))
            }
        }
    }

    fun writeData(typ: TypeDef, data: Array<TestObject>, conn: Connection) {
        val b: InsertStatementBuilder = InsertStatementBuilder(typ.name, conn)
        for (rowNum:Int in (0..data.size - 1)) {
            val testObject: TestObject = data[rowNum]

            b.add("id", testObject.oid)
            for (col:Int in (0..typ.fields.size - 1)) {
                val field = typ.fields[col]
                val cellData = testObject.values[col]

                if (field.typ == Types.array) {
                    writeData(field.refType!!, cellData as Array<TestObject>, conn)
                }
                else
                {
                    b.add(field.name, cellData)
                }
            }
            b.addBatch()
        }
        b.executeBatch()
    }

    fun sqltype(t: org.funql.ri.test.genericobject.Types): String {
        return when (t) {
            Types.string -> "VARCHAR(8192)"
            Types.array, Types.obj -> ""
            Types.float -> "REAL"
            Types.bool -> "BOOLEAN"
            Types.obj -> "OTHER"
            Types.date -> "DATE"
            Types.int, Types.key, Types.ref -> "BIGINT"
            else -> throw IllegalArgumentException(t.toString())
        }
    }

    fun ResultSet.getColumnNames(): Array<String> {
        val meta = getMetaData()
        return Array<String>(meta.getColumnCount(), { meta.getColumnName(it + 1) ?: it.toString() })
    }

    protected fun dump(its: FqlIterator): String {
        val b: StringBuilder = StringBuilder("[\n")
        val any = its.next()
        if (any == FqlIterator.sentinel)
            return "[]\n"
        val rs = any as ResultSet

        val columnNames = rs.getColumnNames()
        do{
            b.append("{")

            for (col: Int in (1..columnNames.size)){
                if (col > 1)
                    b.append(", ")
                b.append(columnNames[col - 1])
                b.append(": ")
                dumpString(rs.getObject(col), b)
            }
            b.append("},\n")

        } while (rs.next())
        b.deleteCharAt(b.size - 2)
        b.append("]\n")
        return b.toString()

    }

    fun readAll(sql: String): FqlMaterializedQuery {
        val st = conn.createStatement()!!;
        val resultSet = st.executeQuery(sql)
        val columnNames = resultSet.getColumnNames()
        val list = ArrayList<Array<Any?>>();
        while (resultSet.next())
        {
            val values = resultSet.getValues(columnNames)
            list.add(values)
        }
        val ret = FqlMaterializedQuery(columnNames, list)
        return ret

    }

}
