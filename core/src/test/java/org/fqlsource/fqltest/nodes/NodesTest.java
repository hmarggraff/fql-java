package org.fqlsource.fqltest.nodes;


import org.fqlsource.data.FqlDataException;
import org.fqlsource.exec.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for simple MockDriver.
 */

public class NodesTest extends NodeTestBase
{

    @BeforeClass
    public static void init() throws FqlDataException
    {
        createNodes();
    }

    @Test
    public void testDump() throws FqlDataException
    {
        System.out.println(dumpNode(in0));
        System.out.println(dumpNode(bnf));
        System.out.println(dumpNode(nilNode));
        System.out.println(dumpNode(setNode));
        System.out.println(dumpNode(sng));
        System.out.println(dumpNode(new AndNode(in1, nilNode, 1, 1)));
    }

    CharSequence dumpNode(final FqlNodeInterface node)
    {
        StringBuffer sb = new StringBuffer();
        node.dump(sb);
        return sb;
    }

    /**
     * Tests if constant value nodes work
     *
     * @throws org.fqlsource.data.FqlDataException
     *          Thrown if entry point access fails in driver
     */
    @Test
    public void testConsts() throws FqlDataException
    {
        Assert.assertEquals(0l, ((Number) in0.getValue(env, null)).longValue());
        Assert.assertEquals(2.0f, ((Number) fn2.getValue(env, null)).floatValue(), 0.0);
        Assert.assertEquals("", sne.getValue(env, null));
        Assert.assertNull(nilNode.getValue(env, null));
        Assert.assertTrue(((Boolean) bnt.getValue(env, null)).booleanValue());
    }

    /**
     * Tests if simple operator nodes work
     *
     * @throws org.fqlsource.data.FqlDataException
     *          Thrown if entry point access fails in driver
     */
    @Test
    public void testOperators() throws FqlDataException
    {
        AndNode ant = new AndNode(bnt, bnt, 1, 1);
        Assert.assertTrue(((Boolean) ant.getValue(env, null)).booleanValue());

        AndNode anf = new AndNode(bnf, bnt, 1, 1);
        Assert.assertFalse(((Boolean) anf.getValue(env, null)).booleanValue());

        AndNode ann = new AndNode(nilNode, bnt, 1, 1);
        Assert.assertFalse(((Boolean) ann.getValue(env, null)).booleanValue());

        AndNode ann2 = new AndNode(nilNode, nilNode, 1, 1);
        Assert.assertFalse(((Boolean) ann2.getValue(env, null)).booleanValue());

        for (FqlNode node : new FqlNode[]{arrayNode, listNode, setNode})
        {
            System.out.println(node.getValue(env, null).getClass().getName());
            checkCollectionSlice(node, in0, in2, 2);
            checkCollectionSlice(node, in1, in2, 1);
            checkCollectionSlice(node, in0, in6, 6);
            checkCollectionSlice(node, in1, in9, 5);
            checkCollectionSlice(node, in0, in0, 0);
            checkCollectionSlice(node, in1, in0, 0);
            checkCollectionSlice(node, in6, in9, 0);
        }
    }

    void checkCollectionSlice(final FqlNode arrayVal, final ConstIntNode start, final ConstIntNode end, int len) throws FqlDataException
    {
        CollectionSliceNode csn = new CollectionSliceNode(arrayVal, start, end, 1, 1);
        final Object csVal = csn.getValue(env, null);
        if ((csVal == null && len == 0))
        {
            return; // no elements: nothing more to check
        }
        Assert.assertTrue(csVal instanceof Iterable);
        int cnt = 0;
        Iterable itVal = (Iterable) csVal;
        for (Object s : itVal)
        {
            Assert.assertEquals(stringArray[(int) start.getIntVal() + cnt], s);
            cnt++;
        }
        Assert.assertEquals(len, cnt);
    }


    /**
     * Tests if getStream properly detects and signals a non existing entry point
     * @throws org.fqlsource.data.FqlDataException is expected
     */
    @Test(expected = FqlDataException.class)
    public void testExceptions() throws FqlDataException
    {
        AndNode ann2 = new AndNode(in1, nilNode, 1, 1);
        ann2.getValue(env, null);
        Assert.fail("Exception not thrown" + dumpNode(ann2));
    }
}
