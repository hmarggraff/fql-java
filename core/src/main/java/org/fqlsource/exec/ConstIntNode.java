package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class ConstIntNode extends FqlNode
{
    public final long intVal;

    public ConstIntNode(long intVal, int row, int col)
    {
        super(row, col);
        this.intVal = intVal;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return intVal;
    }

    private boolean isInteger()
    {
        return true;
    }
}
