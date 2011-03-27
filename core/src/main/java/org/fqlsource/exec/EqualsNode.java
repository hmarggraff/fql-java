package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.RunEnv;

/**
 */
public class EqualsNode extends BinaryNode
{
    public EqualsNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftValue = left.getValue(env, from);
        Object rightValue = right.getValue(env, from);
        if (leftValue == null)
            return rightValue == null;
        else
            return leftValue.equals(rightValue);
    }
}
