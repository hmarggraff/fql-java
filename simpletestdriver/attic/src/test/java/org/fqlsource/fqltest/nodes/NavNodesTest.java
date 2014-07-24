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

import org.funql.ri.data.*;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.node.DotNode;
import org.funql.ri.exec.node.MemberNode;
import org.funql.ri.simpletestdriver.SimpleTestConnection;
import org.funql.ri.exec.EntryPointSlot;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;

/**
 * Unit test for simple SimpleTestDriver.
 */

public class NavNodesTest extends NodeTestBase
{
    static RunEnv env;
    protected static EntryPointSlot epI3;
    protected static FqlIterator source;

    @BeforeClass
    public static void openConnction() throws FqlDataException
    {
	Map<String, String> p = new HashMap<String, String>();
	p.put("driver", "SimpleTestDriver");
	p.put("count", "1");
	FunqlConnection conn = new SimpleTestConnection("simpleTest", p);

	env = new RunEnv(1,1,1,null);
	env.setConnectionAt(0,conn);
	source = conn.getIterator("I3");
	env.setIterator(0, source);

	epI3 = new EntryPointSlot(conn.getName(), 0, "I3", 0);

    }

    @Test
    public void testAccessNode() throws FqlDataException
    {
	MemberNode an = new MemberNode("s1", epI3, 1, 1);
	while (source.hasNext())
	{
	    Object o = source.next();
	    Object value = an.getValue(env, o);
	    Assert.assertNotNull(value);
	    Assert.assertThat(value, instanceOf(String.class));
	    Assert.assertTrue(((String) value).endsWith(".s1"));
	}
	env.setIterator(0, null);
    }

    @Test
    public void testDotNode() throws FqlDataException
    {
	MemberNode an = new MemberNode("s1", epI3, 1, 1);
	DotNode dn = new DotNode(an, "d1", epI3, 1, 1);
	int loopCnt = 1;
	while (source.hasNext())
	{
	    Object o = source.next();
	    Object value = dn.getValue(env, o);
	    Assert.assertNotNull(value);
	    Assert.assertThat(value, instanceOf(String.class));
	    Assert.assertTrue(((String) value).equals(loopCnt + ".s1.d1"));
	    loopCnt++;
	}
	env.setIterator(0, null);
    }
}
