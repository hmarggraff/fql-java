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
import org.testng.annotations.Test
import org.testng.Assert
import org.funql.ri.util.ConfigurationError
import org.funql.ri.sqldriver.scanning.SqlScanConnectionWithPreloadedDriver

/**
 * Unit test for simple Sql Driver.
 */
class SiSqlDriverTest
{

    fun openConnction(name: String): SqlScanConnectionWithPreloadedDriver
    {
        val p = HashMap<String, String>()
        p.put("driver", "org.funql.ri.sqldriver.scanning.SiSqlDriver")
        p.put("connection", "jdbc:hsqldb:mem:" + name)
        p.put("user", "SA")
        p.put("password", "")
        p.put("driver_class", "org.hsqldb.jdbc.JDBCDriver")
        return SqlScanConnectionWithPreloadedDriver("name", p)
    }

    /**
     * Tests if connection to an in memory db can be made
     *
     */
    Test fun testCreate()
    {

        val conn = openConnction("testdb")
        Assert.assertNotNull(conn)
        conn.close()
    }

    /**
     * Tests if connection with missing parameters fails
     *
     * @throws org.fqlsource.data.FqlDataException Thrown if entry point access fails in driver
     */
    Test fun testApp()
    {
        val name = "failing"
        var conn: SqlScanConnectionWithPreloadedDriver? = null
        try {
            conn = SqlScanConnectionWithPreloadedDriver(name, mapOf("connection" to "jdbc:hsqldb:mem:" + name, "user" to "SA"))
        } catch (x: ConfigurationError) {
            Assert.assertEquals(x.getMessage(), "Simple Sql driver needs properties: connection, user, password, driver_class")
        }
        finally {
            conn?.close()
        }
    }

    Test fun listTable()
    {
        val name = "failing"
        var conn: SqlScanConnectionWithPreloadedDriver? = null
        try {
            conn = SqlScanConnectionWithPreloadedDriver(name, mapOf("connection" to "jdbc:hsqldb:mem:" + name, "user" to "SA"))
        } catch (x: ConfigurationError) {
            Assert.assertEquals(x.getMessage(), "Simple Sql driver needs properties: connection, user, password, driver_class")
        }
        finally {
            conn?.close()
        }
    }



    fun createtables()
    {

    }
}
