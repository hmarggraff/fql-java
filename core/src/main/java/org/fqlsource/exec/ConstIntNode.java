package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class ConstIntNode extends FqlNode
{
    public final long intVal;

    public ConstIntNode(long intVal)
    {
        super(row, col);
        this.intVal = intVal;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return this;
    }

    private boolean isInteger()
    {
        return true;
    }
}
