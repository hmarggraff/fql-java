package org.fqlsource.fqltest.mockdriver;

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

import org.fqlsource.data.FqlDataException;
import org.fqlsource.exec.FqlIterator;
import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.fqlsource.parser.FqlParser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;


@RunWith(value = Parameterized.class)
public class Queries
{
    String query;
    String result;
    static MockDriverConnection conn;
    static ArrayList<Object[]> testParameters = new ArrayList<Object[]>();
    static Yaml yaml;

    static void add(String query, String result)
    {
        testParameters.add(new Object[]{query, result});
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data()
    {
        add("from e1", "e1");
        return testParameters;
    }

    public Queries(String query, String result)
    {
        this.query = query;
        this.result = result;
    }

    @BeforeClass
    public static void bc() throws FqlDataException
    {
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        final MockDriver mockDriver = new MockDriver();
        conn = mockDriver.open(p);
        yaml = new Yaml();
    }

    @Test
    public void test2() throws Exception
    {
        final FqlIterator it = FqlParser.runQuery(query,null,conn);
        ArrayList<Object[]> result = new ArrayList<Object[]>();
        while (it.hasNext())
        {
            final Object[] next = (Object[]) it.next();
            result.add(next);
        }
        final String dump = yaml.dump(result);
        System.out.println(dump);
    }


    @AfterClass
    public static void log()
    {
        // nothing
    }
}
