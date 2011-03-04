package org.fqlsource.fqltest.nodes;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.exec.*;
import org.junit.BeforeClass;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: hmf
 * Date: 28.02.11
 * Time: 19:40
 * To change this template use File | Settings | File Templates.
 */
public class NodeTestBase
{
    static RunEnv env;
    static ConstIntNode in0;
    static ConstIntNode in1;
    static ConstIntNode in2;
    static ConstIntNode in6;
    static ConstIntNode in9;
    static ConstFloatNode fn2;
    static ConstBooleanNode bnt;
    static ConstBooleanNode bnf;
    static NilNode nilNode;
    static ConstStringNode sne;
    static ConstStringNode sng;
    static String[] stringArray;
    static List<String> listVal;
    static SortedSet<String> setVal;
    static TestValueNode arrayNode;
    static TestValueNode listNode;
    static TestValueNode setNode;

    @BeforeClass
    public static void createNodes() throws FqlDataException
    {
        in0 = new ConstIntNode(0, 1, 1);
        in1 = new ConstIntNode(1, 1, 1);
        in2 = new ConstIntNode(2, 1, 1);
        in6 = new ConstIntNode(6, 1, 1);
        in9 = new ConstIntNode(9, 1, 1);
        fn2 = new ConstFloatNode(2.0, 1, 1);
        bnt = new ConstBooleanNode(true, 1, 1);
        bnf = new ConstBooleanNode(false, 1, 1);
        nilNode = new NilNode(1, 1);
        sne = new ConstStringNode("", 1, 1);
        sng = new ConstStringNode("Germering", 1, 1);
        env = new RunEnv();
        stringArray = new String[]{"a", "b", "c", "d", "e", "f"};
        arrayNode = new TestValueNode(stringArray);
        listVal = Arrays.asList(stringArray);
        listNode = new TestValueNode(listVal);
        setVal = new TreeSet<String>(listVal);
        setNode = new TestValueNode(setVal);
    }
}
