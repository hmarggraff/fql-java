package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 */
public class PlusNode extends BinaryNode
{
    public PlusNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
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
                throw new FqlDataException("Left operand of + is a number, but right operand is not. (" + rightValue.getClass() + ")", this);
            if (leftValue instanceof Float || leftValue instanceof Double || rightValue instanceof Float || rightValue instanceof Double)
            {
                double rNum = ((Number) rightValue).doubleValue();
                return (((Number) leftValue).doubleValue()) + rNum;
            }
            else if ((leftValue instanceof Long || leftValue instanceof Integer || leftValue instanceof Byte || leftValue instanceof Short)
                  && (rightValue instanceof Long || rightValue instanceof Integer || rightValue instanceof Byte || rightValue instanceof Short))
            {
                long rNum = ((Number) rightValue).longValue();
                return ((Number) leftValue).longValue() + rNum;
            }
        }
        else if (leftValue instanceof String && rightValue instanceof String)
        {
            String lv = (String) leftValue;
            String rv = (String) rightValue;
            return lv + rv;
        }
        else if ((leftValue instanceof Collection || leftValue.getClass().isArray() || leftValue instanceof Iterable)
              &&
              (rightValue instanceof Collection || rightValue.getClass().isArray() || rightValue instanceof Iterable))
        {
            return new ConcatenatedCollection(leftValue, rightValue, row, col);
        }
        throw new FqlDataException("Cannot add classes " + leftValue.getClass() + " with " + rightValue.getClass(), this);
    }
}
