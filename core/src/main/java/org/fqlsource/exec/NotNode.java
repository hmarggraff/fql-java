package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NotNode extends UnaryNode
{

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object value = right.getValue(env, from);
        if (value instanceof Boolean)
            return !((Boolean) value).booleanValue();
        else
            throw new FqlDataException("Operand of not is not a boolean.", this);
    }

    public NotNode(FqlNodeInterface right, int row, int col)
    {
        super(right, row, col);
    }
}
