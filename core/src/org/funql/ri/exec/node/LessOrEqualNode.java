package org.funql.ri.exec.node;

import org.funql.ri.util.NotYetImplementedError;
import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 */
public class LessOrEqualNode extends BinaryNode
{
    public LessOrEqualNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftValue = left.getValue(env, from);
        Object rightValue = operand.getValue(env, from);
        if (leftValue == null)
        {
            return rightValue != null; //null < everything
        }
        else if (leftValue instanceof Number)
        {
            if (!(rightValue instanceof Number))
                throw fqlDataException("Left operand of <= is a number, but right operand is not. (" + rightValue.getClass() + ")");
            if (leftValue instanceof Float || leftValue instanceof Double || rightValue instanceof Float || rightValue instanceof Double)
            {
                double rNum = ((Number) rightValue).doubleValue();
                return (((Number) leftValue).doubleValue()) <= rNum;
            }
            else if (leftValue instanceof Long || leftValue instanceof Integer || leftValue instanceof Byte || leftValue instanceof Short
                  || rightValue instanceof Long || rightValue instanceof Integer || rightValue instanceof Byte || rightValue instanceof Short)
            {
                long rNum = ((Number) rightValue).longValue();
                return ((Number) leftValue).longValue() <= rNum;
            }
            else if (leftValue instanceof BigInteger || leftValue instanceof BigDecimal || rightValue instanceof BigInteger || rightValue instanceof BigDecimal)
            {
                throw new NotYetImplementedError("BigDecimal/BigInteger");
            }
        }
        else if (leftValue instanceof String && rightValue instanceof String)
        {
            String lv = (String) leftValue;
            String rv = (String) rightValue;
            return lv.compareTo(rv) <= 0;
        }
        throw fqlDataException("Comparing (<=) classes " + leftValue.getClass() + " with " + rightValue.getClass());
    }

    @Override
    public String getOperator() {
        return "<=";
    }
}
