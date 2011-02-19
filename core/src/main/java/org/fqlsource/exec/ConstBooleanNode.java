package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class ConstBooleanNode extends FqlNode
{
    public static ConstBooleanNode TRUE = new ConstBooleanNode(true);
    public static ConstBooleanNode FALSE = new ConstBooleanNode(false);
    public final boolean booleanVal;

    private ConstBooleanNode(boolean boolConst)
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
