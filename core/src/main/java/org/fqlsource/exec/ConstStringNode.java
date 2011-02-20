package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class ConstStringNode extends FqlNode
{
    public final String stringVal;

    public ConstStringNode(String stringConst, int row, int col)
    {
        super(row, col);
        this.stringVal = stringConst;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return stringVal;
    }
}
