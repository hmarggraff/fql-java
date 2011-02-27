package org.fqlsource.fqltest.nodes;


import junit.framework.TestCase;
import org.fqlsource.data.FqlDataException;
import org.junit.Assert;
import org.fqlsource.exec.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

/**
 * Unit test for simple MockDriver.
 */

public class NodesTest extends TestCase
{
    RunEnv env;
    ConstIntNode in0;
    private ConstIntNode in1;
    private ConstIntNode in2;
    private ConstIntNode in6;
    private ConstIntNode in9;
    private ConstFloatNode fn2;
    private ConstBooleanNode bnt;
    private ConstBooleanNode bnf;
    private NilNode nilNode;
    private ConstStringNode sne;
    private ConstStringNode sng;
    private String[] stringArray;
    private List<String> listVal;
    private SortedSet<String> setVal;
    TestValueNode arrayNode;
    TestValueNode listNode;
    TestValueNode setNode;

    

    @BeforeClass
    public void openConnction() throws FqlDataException
    {
        in0 = new ConstIntNode(0,1,1);
        in1 = new ConstIntNode(1,1,1);
        in2 = new ConstIntNode(2,1,1);
        in6 = new ConstIntNode(6,1,1);
        in9 = new ConstIntNode(9,1,1);
        fn2 = new ConstFloatNode(2.0,1,1);
        bnt = new ConstBooleanNode(true,1,1);
        bnf = new ConstBooleanNode(false,1,1);
        nilNode = new NilNode(1,1);
        sne = new ConstStringNode("",1,1);
        sng = new ConstStringNode("Germering",1,1);
        env = new RunEnv();
        stringArray = new String[]{"a", "b", "c", "d", "e", "f"};
        arrayNode = new TestValueNode(stringArray);
        listVal = Arrays.asList(stringArray);
        listNode = new TestValueNode(listVal);
        setVal = new TreeSet<String>(listVal);
        setNode = new TestValueNode(setVal);
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

        for (FqlNode node: new FqlNode[] {arrayNode, listNode, setNode})
        {
            System.out.println(node.getValue(env,null).getClass().getName());
            checkCollectionSlice(node, in0, in2, 2);
            checkCollectionSlice(node, in1, in2, 1);
            checkCollectionSlice(node, in0, in6, 6);
            checkCollectionSlice(node, in1, in9, 5);
            checkCollectionSlice(node, in0, in0, 0);
            checkCollectionSlice(node, in1, in0, 0);
            checkCollectionSlice(node, in6, in9, 0);
        }
    }

    private void checkCollectionSlice(final FqlNode arrayVal, final ConstIntNode start, final ConstIntNode end, int len)
          throws FqlDataException
    {
        CollectionSliceNode csn = new CollectionSliceNode(arrayVal, start, end, 1,1);
        final Object csVal = csn.getValue(env, null);
        if ((csVal == null && len == 0))
            return; // no elements: nothing more to check
        Assert.assertTrue(csVal instanceof Iterable);
        int cnt = 0;
        Iterable<String> itVal = (Iterable<String>) csVal;
        for (String s : itVal)
        {
            Assert.assertEquals(stringArray[(int)start.getIntVal() + cnt], s);
            cnt++;
        }
        Assert.assertEquals(len, cnt);
    }


    /**
     * Tests if getEntryPoint properly detects and signals a non existing entry point
     */
    @Test(expected = FqlDataException.class)
    public void testExceptions() throws FqlDataException
    {
        AndNode ann2 = new AndNode(in1, nilNode, 1,1);
        ann2.getValue(env,null);
        Assert.fail("Exception not thrown");
    }
}
