package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

/**
 */
public class AndNode extends BinaryNode
{
    public AndNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftValue = left.getValue(env, from);
        if (leftValue == null)
            return false;
        checkClassOf(leftValue, Boolean.class, "Left", "and");
        Object rightValue = operand.getValue(env, from);
        if (rightValue == null)
            return false;
        checkClassOf(rightValue, Boolean.class, "Right", "and");

        if (leftValue == null || rightValue == null)
            return false;

        return ((Boolean)leftValue) && ((Boolean)rightValue);
    }
}
