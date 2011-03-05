package org.fqlsource.fqltest.mockdriver;

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

import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.fqlsource.parser.FqlParseException;
import org.fqlsource.parser.FqlParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.Properties;

public class MockDriverQueriesTest
{
    /**
     * Tests the minimal query against the mock driver
     *
     * @throws Exception
     */
    @Test
    public void testFrom() throws Exception
    {
        /**
         * The test query: generates and returns 5 objects.
         */
        String[] q = {
          "from e5 in conn"
        };
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        final MockDriver mockDriver = new MockDriver();
        final MockDriverConnection conn = mockDriver.open(p);
        for (String query : q)
        {
            Iterator iterator = FqlParser.runQuery(query, null, conn);
            while (iterator.hasNext())
            {
                Object next = iterator.next();
                System.out.println(next);
            }
        }
    }

    /**
     * Tests the minimal query against the mock driver
     *
     * @throws Exception
     */
    @Test
    public void testUse() throws Exception
    {
        /**
         * The test query: generates and returns 5 objects.
         */
        String[] q = {
          "use abc in provided_connection from e1 in provided_connection", "use abc from e1", "use \"x y\" as xy from e1", "from \"bla blubb\" as e1"
        };
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        final MockDriver mockDriver = new MockDriver();
        final MockDriverConnection conn = mockDriver.open(p);
        for (String query : q)
        {
            Iterator iterator = FqlParser.runQuery(query, null, conn);
            while (iterator.hasNext())
            {
                Object next = iterator.next();
                //TODO check test results
            }
        }
    }

    /**
     * Tests the minimal query against the mock driver
     *
     * @throws Exception
     */
    @Test
    public void testErrorsUseFrom() throws Exception
    {
        /**
         * The test query: generates and returns 5 objects.
         */
        String[] q = {
          "use \"x y\" from e1", "from \"bla blubb\"", "use x"
        };
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        final MockDriver mockDriver = new MockDriver();
        final MockDriverConnection conn = mockDriver.open(p);
        for (String query : q)
        {
            try
            {
                Iterator iterator = FqlParser.runQuery(query, null, conn);
                while (iterator.hasNext())
                {
                    Object next = iterator.next();
                    Assert.fail();
                }
            }
            catch (FqlParseException ex)
            {
                // ok;
            }
        }
    }
}
