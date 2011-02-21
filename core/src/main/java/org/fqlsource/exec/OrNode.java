package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class OrNode extends BinaryNode
{
    public OrNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftValue = left.getValue(env, from);
        if (leftValue == null)
            return false;
        checkClassOf(leftValue, Boolean.class, "Left", "or");
        Object rightValue = right.getValue(env, from);
        if (rightValue == null)
            return false;
        checkClassOf(rightValue, Boolean.class, "Right", "or");

        if (leftValue == null || rightValue == null)
            return false;

        return ((Boolean)leftValue) || ((Boolean)rightValue);
    }
}
