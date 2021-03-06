package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

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
        Object rightValue = operand.getValue(env, from);
        if (rightValue == null)
            return false;
        checkClassOf(rightValue, Boolean.class, "Right", "or");

        if (leftValue == null || rightValue == null)
            return false;

        return ((Boolean)leftValue) || ((Boolean)rightValue);
    }

    @Override
    public String getOperator() {
        return "or";
    }
}
