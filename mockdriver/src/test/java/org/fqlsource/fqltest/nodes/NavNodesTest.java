package org.fqlsource.fqltest.nodes;


import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlEntryPoint;
import org.fqlsource.exec.AccessNode;
import org.fqlsource.exec.FqlStatement;
import org.fqlsource.exec.RunEnv;
import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Unit test for simple MockDriver.
 */

public class NavNodesTest extends NodeTestBase
{
    RunEnv env;
    private MockDriver mockDriver;
    private MockDriverConnection conn;

    @BeforeClass
    public void init() throws FqlDataException
    {
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        mockDriver = new MockDriver();
        conn = mockDriver.open(p);
        env = new RunEnv();
    }

    /**
     * Tests if constant value nodes work
     *
     * @throws org.fqlsource.data.FqlDataException
     *          Thrown if entry point access fails in driver
     */
    @Test
    public void testAccess() throws FqlDataException
    {
        AccessNode an = new AccessNode("s1", 1, 1);
        FqlEntryPoint mockEntryPoint = conn.getEntryPoint("e1");
        for (Object o : mockEntryPoint)
        {
            Object value = an.getValue(env, o);
            //Assert.assertNotNull(value);
        }

    }

    void clauses(List<FqlStatement> fqlStatements) throws FqlDataException
    {
        Iterator precedent = null;
        for (FqlStatement statement : fqlStatements)
        {
            precedent = statement.execute(env, precedent).iterator();
        }
    }

    /**
     * Tests if getEntryPoint properly detects and signals a non existing entry point
     */
    //@Test(expected = FqlDataException.class)
    public void testExceptions() throws FqlDataException
    {
        //Assert.fail("Exception not thrown");
    }
}
