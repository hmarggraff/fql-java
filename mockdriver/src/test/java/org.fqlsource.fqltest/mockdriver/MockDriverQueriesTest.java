package org.fqlsource.fqltest.mockdriver;

import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.fqlsource.parser.FqlParser;
import org.junit.Test;

import java.util.Iterator;
import java.util.Properties;

public class MockDriverQueriesTest
{
    /**
     * Tests the minimal query against the mock driver
     * @throws Exception
     */
    @Test
    public void testFrom() throws Exception
    {
        /**
         * The test query: generates and returns 5 objects.
         */
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
