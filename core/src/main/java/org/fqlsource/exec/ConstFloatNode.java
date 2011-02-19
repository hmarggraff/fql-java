package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class ConstFloatNode extends FqlNode
{
    public final double floatVal;

    public ConstFloatNode(double floatVal)
    {
        super(row, col);
        this.floatVal = floatVal;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return this;
    }

    private boolean isFloat()
    {
        return true;
    }
}
