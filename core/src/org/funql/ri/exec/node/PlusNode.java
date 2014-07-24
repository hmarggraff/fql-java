package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.ConcatenatedCollection;

import java.util.Collection;

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
     * @throws org.funql.ri.data.FqlDataException
     */

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftValue = left.getValue(env, from);
        Object rightValue = operand.getValue(env, from);
        if (leftValue == null)
            return rightValue;
        else if (rightValue == null)
            return leftValue;
        else if (leftValue instanceof Number)
        {
            if (!(rightValue instanceof Number))
                throw fqlDataException("Left operand of + is a number, but right operand is not. (" + rightValue.getClass() + ")");
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
        throw fqlDataException("Cannot add classes " + leftValue.getClass() + " with " + rightValue.getClass());
    }

    @Override
    public String getOperator() {
        return "+";
    }

}
