package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

import java.util.Collection;

/**
 */
public class DivideNode extends BinaryNode
{
    public DivideNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
    }

    /**
     *
     * @param env The run time environment object
     * @param from  The parent data object
     * @return a Boolean: true if the pattern matchges the string using javas standard regexp logic
     * @throws org.fqlsource.data.FqlDataException
     */

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftValue = left.getValue(env, from);
        Object rightValue = right.getValue(env, from);
        if (leftValue == null)
            return rightValue;
        else if (rightValue == null)
            return leftValue;
        else if (leftValue instanceof Number)
        {
            if (!(rightValue instanceof Number))
                throw new FqlDataException("Left operand of / is a number, but right operand is not. (" + rightValue.getClass() + ")", this);
            if (leftValue instanceof Float || leftValue instanceof Double || rightValue instanceof Float || rightValue instanceof Double)
            {
                double rNum = ((Number) rightValue).doubleValue();
                return (((Number) leftValue).doubleValue()) / rNum;
            }
            else if ((leftValue instanceof Long || leftValue instanceof Integer || leftValue instanceof Byte || leftValue instanceof Short)
                  && (rightValue instanceof Long || rightValue instanceof Integer || rightValue instanceof Byte || rightValue instanceof Short))
            {
                long rNum = ((Number) rightValue).longValue();
                return ((Number) leftValue).longValue() / rNum;
            }
        }
        throw new FqlDataException("Cannot divide classes " + leftValue.getClass() + " and " + rightValue.getClass(), this);
    }
}
