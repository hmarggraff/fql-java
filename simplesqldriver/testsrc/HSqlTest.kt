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

import org.funql.ri.sisql.SiSqlConnection
import java.util.HashMap
import org.testng.annotations.Test
import org.funql.ri.data.FqlIterator
import java.sql.ResultSet
import org.funql.ri.test.cameradata.CameraData
import org.funql.ri.test.genericobject.Types
import org.funql.ri.util.SkipTest

/**
 * Unit test for simple Sql Driver.
 */
class HSqlTest
{

    fun openConnction(name: String): SiSqlConnection
    {
        val p = HashMap<String, String>()
        p.put("driver", "org.funql.ri.sisql.SiSqlDriver")
        p.put("connection", "jdbc:hsqldb:mem:" + name)
        p.put("user", "SA")
        p.put("password", "")
        p.put("driver_class", "org.hsqldb.jdbc.JDBCDriver")
        return SiSqlConnection("name", p)
    }

    /**
     * Tests if connection to an in memory db can be made
     *
     */
    SkipTest fun systemtables()
    {

        val conn = openConnction("testdb")
        val fqlIterator = conn.getIterator("INFORMATION_SCHEMA.SYSTEM_TABLES")!!
        do  {
            val d = fqlIterator.next()
            if (d == FqlIterator.sentinel)   break;
            val rs = d as ResultSet
            val cols = rs.getColumnNames()
            for (i in (1 ..13))
                println(cols[i] + ": " + rs.getObject(i))
            //cols.forEach { println(it) }

        }   while (true)
        conn.close()
    }

    /**
     * Tests if connection with missing parameters fails
     *
     * @throws org.fqlsource.data.FqlDataException Thrown if entry point access fails in driver
     */
    Test fun testApp()
    {
        createtables()
    }


    fun createtables()
    {

        val b = StringBuilder("create table cameras (\n  id BIGINT")
        CameraData.cameraFields.fields.withIndices().forEach {
            b append ",\n  "
            b append it.second.name
            b append ' '
            b append sqltype(it.second.typ)
        }
        b append "\n)\n"
        println(b)



    }

    fun sqltype(t: org.funql.ri.test.genericobject.Types): String{
        return when (t) {
            Types.string -> "VARCHAR(8192)"
            Types.array, Types.obj -> ""
            Types.float -> "REAL"
            Types.bool -> "BOOLEAN"
            Types.obj -> "OTHER"
            Types.date -> "DATE"
            Types.int, Types.lid, Types.ref -> "BIGINT"
            else -> throw IllegalArgumentException(t.toString())
        }
    }

    fun ResultSet.getColumnNames(): jet.Array<String> {
        val meta = getMetaData()
        return jet.Array<String>(meta.getColumnCount(), { meta.getColumnName(it + 1) ?: it.toString() })
    }


}
