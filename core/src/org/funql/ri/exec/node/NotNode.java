package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

public class NotNode extends UnaryNode
{

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object value = operand.getValue(env, from);
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
