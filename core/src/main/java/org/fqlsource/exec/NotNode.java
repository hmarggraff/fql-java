package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.RunEnv;

public class NotNode extends UnaryNode
{

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object value = right.getValue(env, from);
        if (value instanceof Boolean)
            return !((Boolean) value).booleanValue();
        else
            throw fqlDataException("Operand of not is not a boolean but a " + value.getClass().getName());
    }

    public NotNode(FqlNodeInterface right, int row, int col)
    {
        super(right, row, col);
    }
}
