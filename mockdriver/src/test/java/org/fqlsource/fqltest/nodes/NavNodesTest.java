package org.fqlsource.fqltest.nodes;


import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlDataSource;
import org.fqlsource.exec.AccessNode;
import org.fqlsource.exec.DotNode;
import org.fqlsource.exec.FqlStatement;
import org.fqlsource.exec.RunEnv;
import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.fqlsource.util.NamedIndex;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;

/**
 * Unit test for simple MockDriver.
 */

public class NavNodesTest extends NodeTestBase
{
    static RunEnv env;
    private static MockDriver mockDriver;
    private static MockDriverConnection conn;
    public static FqlDataSource source;
    public static NamedIndex defaultSourceIndex;

    @BeforeClass
    public static void init() throws FqlDataException
    {
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        mockDriver = new MockDriver();
        conn = mockDriver.open(p);
        env = new RunEnv(1,1,null);
        defaultSourceIndex = new NamedIndex("e1", 0);

        source = conn.getSource(defaultSourceIndex.getName());
        env.setSourceAt(defaultSourceIndex.getIndex(), source);
        env.setConnectionAt(0, conn);
    }

    @Test public void testAccessNode() throws FqlDataException
    {
        AccessNode an = new AccessNode(defaultSourceIndex, "s1", 1, 1);
        for (Object o : source)
        {
            Object value = an.getValue(env, o);
            Assert.assertNotNull(value);
            Assert.assertThat(value, instanceOf(String.class));
            Assert.assertTrue(((String) value).endsWith(".s1"));
        }

    }
    @Test public void testDotNode() throws FqlDataException
    {
        AccessNode an = new AccessNode(defaultSourceIndex, "s1", 1, 1);
        DotNode dn = new DotNode(an, "d1", 0, 1,1);
        for (Object o : source)
        {
            Object value = dn.getValue(env, o);
            Assert.assertNotNull(value);
            Assert.assertThat(value, instanceOf(String.class));
            Assert.assertTrue(((String) value).endsWith(".s1.d1"));
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

}
