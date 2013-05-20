package org.fqlsource.fqltest.mockdrivertest;


/*
 Copyright (C) 2011, Hans Marggraff and other copyright owners as documented in the project's IP log.
 This program and the accompanying materials are made available under the terms of the Eclipse Distribution License v1.0 which accompanies this distribution, is reproduced below, and is available at http://www.eclipse.org/org/documents/edl-v10.php
 All rights reserved.
 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 - Neither the name of the Eclipse Foundation, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.funql.ri.data.FunqlConnection;
import org.funql.ri.data.FqlDataException;
import org.fqlsource.fqltest.simpletestdriver.SimpleTestDriver;
import org.fqlsource.fqltest.simpletestdriver.SimpleTestIterator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple SimpleTestDriver.
 */

public class SimpleTestDriverTest
{
    FunqlConnection conn;

    @Before
    public void openConnction() throws FqlDataException
    {
	Map<String, String> p = new HashMap<String, String>();
	p.put("driver", "SimpleTestDriver");
	p.put("count", "1");
	final SimpleTestDriver testDriver = new SimpleTestDriver();
	conn = testDriver.openConnection("simpleTest", p);
    }

    @After
    public void closeConnection()
    {
	conn.close();
    }

    /**
     * Tests if getList properly returns the entry point of the mock driver
     *
     * @throws FqlDataException Thrown if entry point access fails in driver
     */
    @Test
    public void testApp() throws FqlDataException
    {
	Assert.assertNotNull(conn.getIterator("E8080"));
    }

    /**
     * Tests if getList properly detects and signals a non existing entry point
     *
     * @throws org.funql.ri.data.FqlDataException
     *          expected
     */
    @Test(expected = FqlDataException.class)
    public void testApp2() throws FqlDataException
    {
	conn.getIterator("ExceptionTesting");
	Assert.fail("Entry point should not exist");
    }

    /**
     * Tests if the mock driver returns data with the expected generated fields.
     *
     * @throws org.funql.ri.data.FqlDataException
     *
     */
    @Test
    public void testAppFields() throws FqlDataException
    {
	final SimpleTestIterator stream = (SimpleTestIterator) conn.getIterator("E42");

	int count = 1;
	while (stream.hasNext())
	{
	    Object it = stream.next();
	    Assert.assertTrue((Boolean) conn.getMember(it, "yes"));
	    Assert.assertFalse((Boolean) conn.getMember( it, "no"));
	    Assert.assertEquals(conn.getMember( it, "L" + Integer.toString(count)), (long) count);
	    Assert.assertEquals(conn.getMember( it, "S" + count), count + ".S" + count);
	    Assert.assertEquals(conn.getMember( it, "D" + count), (double) count);
	    Assert.assertEquals(count, ((Date) conn.getMember( it, "T" + count)).getTime());
	    count++;
	}
	System.out.println("Basic fields exist. Iterations: "+ count);
    }
}
