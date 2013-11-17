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

import org.funql.ri.data.FqlIterator
import java.sql.ResultSet
import org.funql.ri.test.cameradata.CameraData
import org.funql.ri.test.genericobject.Types
import java.sql.Connection
import org.funql.ri.test.genericobject.TypeDef
import org.funql.ri.test.genericobject.FieldDef
import org.funql.ri.test.genericobject.TestObject
import org.funql.ri.sisql.InsertStatementBuilder
import org.testng.Assert
import kotlin.test.fail
import org.funql.ri.test.util.dumpString


/**
 * Unit test for simple Sql Driver.
 */
open class HSqlCameraDataBase : HSqlTestBase()
{

    {
        createTable(CameraData.cameraFields, conn)
        createTable(CameraData.employeeType, conn)
        createTable(CameraData.organisationFields, conn)
        createTable(CameraData.orderItemType, conn)
        createTable(CameraData.orderType, conn)
        val rs = conn.createStatement()!!.executeQuery("select * from " + "INFORMATION_SCHEMA.SYSTEM_TABLES")

        while (rs.next()) {
            if ("PUBLIC" != rs.getString(2)) continue; // SCHEMA
            val cols = rs.getColumnNames()
            for (i in (1..4))
                print("   ${cols[i - 1]}: ${rs.getObject(i)} ")
            println   ()
        }

        writeChecked(CameraData.orderType, CameraData.orders(), conn)
        writeChecked(CameraData.cameraFields, CameraData.products, conn)
        writeChecked(CameraData.employeeType, CameraData.employees, conn)
        writeChecked(CameraData.organisationFields, CameraData.orgs, conn)
        val itemCount = count(CameraData.orderItemType.name, conn)
        Assert.assertEquals(itemCount, 1519)
    }
}
