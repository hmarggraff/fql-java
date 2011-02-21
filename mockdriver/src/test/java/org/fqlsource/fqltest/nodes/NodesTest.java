package org.fqlsource.fqltest.nodes;


import junit.framework.Assert;
import org.fqlsource.data.FqlDataException;
import org.junit.Before;
import org.junit.Test;
import org.fqlsource.exec.*;

/**
 * Unit test for simple MockDriver.
 */

public class NodesTest
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

    @Before
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
        env = new RunEnv();

    }

    /**
     * Tests if constant value nodes work
     *
     * @throws org.fqlsource.data.FqlDataException Thrown if entry point access fails in driver
     */
    //@Test
    public void testConsts() throws FqlDataException
    {
        Assert.assertEquals(0l, ((Number)in0.getValue(env,null)).longValue());
        Assert.assertEquals(2.0f, ((Number) fn2.getValue(env,null)).floatValue());
        Assert.assertEquals("", sne.getValue(env, null));
        Assert.assertNull(nilNode.getValue(env, null));
        Assert.assertTrue(((Boolean) bnt.getValue(env, null)).booleanValue());
    }
    /**
     * Tests if simple operator nodes work
     *
     * @throws org.fqlsource.data.FqlDataException Thrown if entry point access fails in driver
     */
    @Test
    public void testOperators() throws FqlDataException
    {
        AndNode ant = new AndNode(bnt, bnt, 1,1);
        Assert.assertTrue(((Boolean) ant.getValue(env, null)).booleanValue());

        AndNode anf = new AndNode(bnf, bnt, 1,1);
        Assert.assertFalse(((Boolean) anf.getValue(env, null)).booleanValue());

        AndNode ann = new AndNode(nilNode, bnt, 1,1);
        Assert.assertFalse(((Boolean) ann.getValue(env, null)).booleanValue());

        AndNode ann2 = new AndNode(nilNode, nilNode, 1,1);
        Assert.assertFalse(((Boolean) ann2.getValue(env, null)).booleanValue());

    }


    /**
     * Tests if getEntryPoint properly detects and signals a non existing entry point
     */
    @Test(expected = FqlDataException.class)
    public void testExceptions() throws FqlDataException
    {
        Assert.fail("Exception not thrown");
    }
}
