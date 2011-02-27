package org.fqlsource.fqltest.mockdriver;

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
          "from e5"
        };
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        final MockDriver mockDriver = new MockDriver();
        final MockDriverConnection conn = mockDriver.open(p);
        for (String query : q)
        {
            Iterator iterator = FqlParser.runQuery(query, conn);
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
          "use abc from e1", "use \"x y\" as xy from e1", "from \"bla blubb\" as e1"
        };
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        final MockDriver mockDriver = new MockDriver();
        final MockDriverConnection conn = mockDriver.open(p);
        for (String query : q)
        {
            Iterator iterator = FqlParser.runQuery(query, conn);
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
                Iterator iterator = FqlParser.runQuery(query, conn);
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
