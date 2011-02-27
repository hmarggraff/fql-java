package org.fqlsource.fqltest.nodes;


import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlEntryPoint;
import org.fqlsource.exec.*;
import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

/**
 * Unit test for simple MockDriver.
 */

public class NavNodesTest
{
    RunEnv env;
    ConstIntNode in0;
    private ConstIntNode in1;
    private ConstIntNode in2;
    private ConstFloatNode fn2;
    private ConstBooleanNode bnt;
    private ConstBooleanNode bnf;
    private NilNode nilNode;
    private ConstStringNode sne;
    private ConstStringNode sng;
    private MockDriver mockDriver;
    private MockDriverConnection conn;

    @BeforeClass
    public void openConnction() throws FqlDataException
    {
        in0 = new ConstIntNode(0,1,1);
        in1 = new ConstIntNode(1,1,1);
        in2 = new ConstIntNode(2,1,1);
        fn2 = new ConstFloatNode(2.0,1,1);
        bnt = new ConstBooleanNode(true,1,1);
        bnf = new ConstBooleanNode(false,1,1);
        nilNode = new NilNode(1,1);
        sne = new ConstStringNode("",1,1);
        sng = new ConstStringNode("Germering",1,1);
        String q = "from e5";
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
     * @throws org.fqlsource.data.FqlDataException Thrown if entry point access fails in driver
     */
    @Test
    public void testAccess() throws FqlDataException
    {
        AccessNode an = new AccessNode("s1", 1,1);
        FqlEntryPoint mockEntryPoint = conn.getEntryPoint("e1");
        for (Object o : mockEntryPoint)
        {
            Object value = an.getValue(env, o);
            Assert.assertNotNull(value);
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
