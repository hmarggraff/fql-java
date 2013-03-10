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

package org.fqlsource.fqltest.jsondrivertest

import kotlin.test.assertEquals
import org.funql.ri.jsondriver.JsonConnection
import java.util.HashMap
import java.util.ArrayList
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.data.FqlDataException
import org.junit.Test
import org.junit.Assert
import org.funql.ri.data.FqlMultiMapContainer
import org.funql.ri.data.FqlIterator

/**
 * Unit test for simple MockDriver.
 */
class JsonDriverTest
{

    fun openConnction(txt: String): JsonConnection
    {
        val p = HashMap<String, String>()
        p.put("driver", "JsonDriver")
        p.put("text", txt)
        //throw Exception("bal")
        return JsonConnection("Json", p)
    }

    /**
    * Tests if getList properly returns the entry point of the mock driver
    *
    * @throws org.fqlsource.data.FqlDataException Thrown if entry point access fails in driver
    */
    Test
            fun testApp()
    {
        val conn = openConnction("[]")
        Assert.assertNotNull(conn.useIterator("top"))
        conn.close()
    }

    /**
     * Tests if getList properly returns the entry point of the mock driver
     *
     * @throws org.fqlsource.data.FqlDataException Thrown if entry point access fails in driver
     */
    Test
            fun testApp3()
    {
        val conn = openConnction("{a: b, c: d}")
        val path = ArrayList<String>()
        path.add("abc")
        val fieldpath = ArrayList<String>()
        fieldpath.add("fun")
        val map: FqlMapContainer = conn.useMap(path)!!
        val lookup = map.lookup("a")
        Assert.assertEquals(lookup, "b")
        conn.close()

    }

    /**
     * Tests if getList properly detects and signals a non existing entry point
     * @throws org.fqlsource.data.FqlDataException expected
     */
    Test
            fun testApp2()
    {
        val conn = openConnction("{a: b, c: d}")
        try {
            conn.useIterator("ExceptionTesting")
            Assert.fail("Entry point should not exist")
        } catch (ex: FqlDataException) {
            // ok
        }
        finally{
            conn.close()
        }
    }

    /**
     * Tests if the mock driver returns data with the expected generated fields.
     *
     */
    Test fun testAppFields()
    {
        val conn = openConnction("[2,3,5,7]")
        val stream = conn.useIterator("top")!!

        var count: Int = 0
        while (stream.hasNext())
        {
            val it = stream.next()

            if (it is Int)
                count = count + it
            else
                throw ClassCastException("json array iterator returns ${it.javaClass} when Int was expected: ${it.toString()}")
        }
        Assert.assertEquals(count, 17)
    }

    Test fun testMultiMap()
    {
        val conn = openConnction("{a: [2,3,5,7]}")
        val mmap: FqlMultiMapContainer = conn.useMultiMap(listOf("top"))!!

        val stream: FqlIterator = mmap.lookup("a")!!

        var count: Int = 0
        while (stream.hasNext())
        {
            val it = stream.next()

            if (it is Int)
                count = count + it
            else
                throw ClassCastException("json array iterator returns ${it.javaClass} when Int was expected: ${it.toString()}")
        }
        Assert.assertEquals(count, 17)
    }


}
