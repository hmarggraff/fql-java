package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

import java.math.BigDecimal;
import java.math.BigInteger;

public class UnaryMinusNode extends UnaryNode
{
    private final BigDecimal big_decimal_chs = new BigDecimal(-1);

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object value = right.getValue(env, from);
        if (value instanceof Float || value instanceof Double)
            return ((Number) value).doubleValue() * -1.0;
        else if (value instanceof Long || value instanceof Integer || value instanceof Byte || value instanceof Short)
            return ((Number) value).longValue() * -1;
        else if (value instanceof BigDecimal)
            return ((BigDecimal) value).multiply(big_decimal_chs);
        else if (value instanceof BigInteger)
            return ((BigInteger) value).multiply(BigInteger.valueOf(-1));
        else
            throw new FqlDataException("Operand of unary minus is not a number.", this);
    }

    public UnaryMinusNode(FqlNodeInterface right, int row, int col)
    {
        super(right, row, col);
    }
}
