package org.fqlsource.fqltest.mockdrivertest;


/*
 *
 *  * Copyright (C) 2011, Hans Marggraff
 *  * and other copyright owners as documented in the project's IP log.
 *  *
 *  * This program and the accompanying materials are made available
 *  * under the terms of the Eclipse Distribution License v1.0 which
 *  * accompanies this distribution, is reproduced below, and is
 *  * available at http://www.eclipse.org/org/documents/edl-v10.php
 *  *
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or
 *  * without modification, are permitted provided that the following
 *  * conditions are met:
 *  *
 *  * - Redistributions of source code must retain the above copyright
 *  *   notice, this list of conditions and the following disclaimer.
 *  *
 *  * - Redistributions in binary form must reproduce the above
 *  *   copyright notice, this list of conditions and the following
 *  *   disclaimer in the documentation and/or other materials provided
 *  *   with the distribution.
 *  *
 *  * - Neither the name of the Eclipse Foundation, Inc. nor the
 *  *   names of its contributors may be used to endorse or promote
 *  *   products derived from this software without specific prior
 *  *   written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 *  * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *  * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 *  * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

import org.fqlsource.data.FqlConnection;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.fqltest.mockdriver.MockDriver;
import org.fqlsource.fqltest.mockdriver.MockStreamContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

/**
 * Unit test for simple MockDriver.
 */

public class MockDriverTest
{
    MockDriver mockDriver;
    FqlConnection conn;

    @Before
    public void openConnction() throws FqlDataException
    {
        Properties p = new Properties();
        p.put("driver", "MockDriver");
        p.put("count", "1");
        mockDriver = new MockDriver();
        conn = mockDriver.open(p);
    }

    @After
    public void closeConnection()
    {
        conn.close();
        mockDriver.close();
    }

    /**
     * Tests if getStream properly returns the entry point of the mock driver
     *
     * @throws FqlDataException Thrown if entry point access fails in driver
     */
    @Test
    public void testApp() throws FqlDataException
    {
        MockStreamContainer.defaultEntryPointName = "nowhere";
        Assert.assertNotNull(getEntryPoint(MockStreamContainer.defaultEntryPointName));
        //assertTrue(true);
    }

    private MockStreamContainer getEntryPoint(final String entryPointName) throws FqlDataException
    {
        final MockStreamContainer ep = (MockStreamContainer) mockDriver.getStream(entryPointName, conn);
        return ep;
    }

    /**
     * Tests if getStream properly detects and signals a non existing entry point
     */
    @Test(expected = FqlDataException.class)
    public void testApp2() throws FqlDataException
    {
        getEntryPoint("fail");
        Assert.fail("Entry point should not exist");
    }

    /**
     * Tests if the mock driver returns data with the expected generated fields.
     *
     * @throws FqlDataException
     */
    @Test
    public void testAppFields() throws FqlDataException
    {
        final MockStreamContainer entryPoint = getEntryPoint(MockStreamContainer.defaultEntryPointName);
        final MockDriver driver = entryPoint.getConnection().getDriver();

        int count = 1;
        while (entryPoint.hasNext())
        {
            Object it = entryPoint.next();
            Assert.assertTrue(driver.getBoolean(it, "yes", entryPoint));
            Assert.assertFalse(driver.getBoolean(it, "no", entryPoint));
            Assert.assertEquals(driver.getLong(it, "L" + Integer.toString(count), entryPoint), count);
            Assert.assertEquals(driver.getString(it, "S" + count, entryPoint), "S" + count);
            Assert.assertEquals(driver.getDate(it, "D" + count, entryPoint).getTime(), count);
            count++;
        }
        System.out.println("Basic fields exist");
    }
}
