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
import java.sql.Connection
import java.sql.DriverManager
import org.funql.ri.test.genericobject.TypeDef
import org.funql.ri.test.genericobject.FieldDef
import org.funql.ri.test.genericobject.TestObject
import org.funql.ri.sisql.InsertStatementBuilder
import org.testng.Assert
import kotlin.test.fail
import org.funql.ri.parser.FqlParser


/**
 * Unit test for simple Sql Driver.
 */
class HSqlQueryTest: HSqlCameraDataBase()
{
    SkipTest fun relQuery(){
        run("from Organisation", "")

    }
    SkipTest fun relQuerySel(){
        run("from Organisation select CITY", "")
    }



    fun run(q: String, expect: Any)
    {
        val iter = FqlParser.runQuery(q, null, sisConn)!!
        val strRes = dump(iter)
        println(strRes)
        Assert.assertEquals(strRes, expect)
        conn.close()
    }

    Test fun sequenceAccess() {
        val st = conn.createStatement()!!;
        st.executeUpdate("create sequence testseq2 as integer");
        val res = sisConn.nextSequenceValue("testseq2")
        Assert.assertEquals(res, 0 as Long)
        val res2 = sisConn.nextSequenceValue("testseq2")
        Assert.assertEquals(res2, 1 as Long)
    }




}
