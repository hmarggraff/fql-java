package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.RunEnv;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 */
public abstract class BinaryNode extends UnaryNode
{
    FqlNodeInterface left;

    protected BinaryNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(right, row, col);
        this.left = left;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "left", "right");
    }

    protected boolean realEqual(RunEnv env, Object from) throws FqlDataException
    {
        Object leftValue = left.getValue(env, from);
        Object rightValue = right.getValue(env, from);
        if (leftValue == null)
            return rightValue == null;
        else if (rightValue == null)
            return false;
        else if (leftValue instanceof Number && rightValue instanceof Number) // number comparison in Java sucks!
        {
            if (leftValue.getClass() == rightValue.getClass()) // no need for promotion
                return leftValue.equals(rightValue);
            else if ((leftValue instanceof Byte || leftValue instanceof Short || leftValue instanceof Integer || leftValue instanceof Long)
                  && (rightValue instanceof Byte || rightValue instanceof Short || rightValue instanceof Integer || rightValue instanceof Long))
            {
                long ll = ((Number) leftValue).longValue();
                long rl = ((Number) rightValue).longValue();
                return ll == rl;
            }
            else if (((leftValue instanceof Double || leftValue instanceof Float) && (rightValue instanceof Double || rightValue instanceof Float || rightValue instanceof Byte || rightValue instanceof Short || rightValue instanceof Integer || rightValue instanceof Long))
                  || ((rightValue instanceof Double || rightValue instanceof Float) && (leftValue instanceof Double || leftValue instanceof Float || leftValue instanceof Byte || leftValue instanceof Short || leftValue instanceof Integer || leftValue instanceof Long)))
            {
                double ld = ((Number) leftValue).doubleValue();
                double rd = ((Number) rightValue).doubleValue();
                return ld == rd;
            }
            else
            {
                final BigDecimal ld;
                if (leftValue instanceof BigDecimal)
                    ld = (BigDecimal) leftValue;
                else if (leftValue instanceof Byte || leftValue instanceof Short || leftValue instanceof Integer || leftValue instanceof Long)
                    ld = new BigDecimal(((Number) leftValue).longValue());
                else if (leftValue instanceof Double || leftValue instanceof Float)
                    ld = new BigDecimal(((Number) leftValue).doubleValue());
                else // if (leftValue instanceof BigInteger)
                    ld = new BigDecimal((BigInteger) leftValue);
                final BigDecimal rd;
                if (rightValue instanceof BigDecimal)
                    rd = (BigDecimal) rightValue;
                else if (rightValue instanceof Byte || rightValue instanceof Short || rightValue instanceof Integer || rightValue instanceof Long)
                    rd = new BigDecimal(((Number) rightValue).longValue());
                else if (rightValue instanceof Double || rightValue instanceof Float)
                    rd = new BigDecimal(((Number) rightValue).doubleValue());
                else // if (rightValue instanceof BigInteger)
                    rd = new BigDecimal((BigInteger) rightValue);
                return ld.equals(rd);
            }
        }
        else
        {
            final boolean isEqual = leftValue.equals(rightValue);
            return isEqual;
        }
    }


}
