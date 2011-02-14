package org.fqlsource.fqltest.mockdriver;

import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.fqlsource.parser.FqlParser;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Properties;

public class MockDriverQueriesTest
{
    @Test
    public void testFrom() throws Exception
    {
        String q = "from e5";
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        final MockDriver mockDriver = new MockDriver();
        final MockDriverConnection conn = mockDriver.open(p);

        Iterator iterator = FqlParser.runQuery(q, conn);
        while (iterator.hasNext())
        {
            Object next = iterator.next();
            System.out.println(next);
        }

    }
}
