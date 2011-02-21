package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class ConstFloatNode extends FqlNode
{
    public final double doubleVal;

    public ConstFloatNode(double doubleVal, int row, int col)
    {
        super(row, col);
        this.doubleVal = doubleVal;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return doubleVal;
    }

    private boolean isFloat()
    {
        return true;
    }
}
