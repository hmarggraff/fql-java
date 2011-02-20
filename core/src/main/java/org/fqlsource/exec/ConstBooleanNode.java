package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class ConstBooleanNode extends FqlNode
{
    public final boolean booleanVal;

    public ConstBooleanNode(boolean boolConst, int row, int col)
    {
        super(row, col);
        this.booleanVal = boolConst;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return this;
    }

    private boolean isBoolean()
    {
        return true;
    }
}
