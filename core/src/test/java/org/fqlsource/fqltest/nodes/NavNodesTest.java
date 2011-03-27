package org.fqlsource.fqltest.nodes;


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

import org.fqlsource.data.*;
import org.fqlsource.exec.*;
import org.fqlsource.fqltest.mockdriver.MockDriverConnection;
import org.fqlsource.util.NamedIndex;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.instanceOf;

/**
 * Unit test for simple MockDriver.
 */

public class NavNodesTest extends NodeTestBase
{
    static RunEnv env;
    public static FqlStreamContainer source;
    public static NamedIndex defaultSourceIndex;

    @BeforeClass
    public static void openConnction() throws FqlDataException
    {
        Map<String, String> p = new HashMap<String, String>();
        p.put("driver", "MockDriver");
        p.put("count", "1");
        FqlConnection conn = new MockDriverConnection();
        conn.init(p);
        env = new RunEnv(1, 1, 0, null);
        defaultSourceIndex = new NamedIndex("e1", 0);

        env.setConnectionAt(0, conn);
        source = conn.getStream(defaultSourceIndex.getName());
        env.pushStream(source);
    }

    @Test
    public void testAccessNode() throws FqlDataException
    {
        AccessNode an = new AccessNode(defaultSourceIndex, "s1", 1, 1);
        while (source.hasNext())
        {
            Object o = source.next();
            Object value = an.getValue(env, o);
            Assert.assertNotNull(value);
            Assert.assertThat(value, instanceOf(String.class));
            Assert.assertTrue(((String) value).endsWith(".s1"));
        }

    }

    @Test
    public void testDotNode() throws FqlDataException
    {
        AccessNode an = new AccessNode(defaultSourceIndex, "s1", 1, 1);
        DotNode dn = new DotNode(an, "d1", 0, 1, 1);
        while (source.hasNext())
        {
            Object o = source.next();
            Object value = dn.getValue(env, o);
            Assert.assertNotNull(value);
            Assert.assertThat(value, instanceOf(String.class));
            Assert.assertTrue(((String) value).endsWith(".s1.d1"));
        }

    }
}
