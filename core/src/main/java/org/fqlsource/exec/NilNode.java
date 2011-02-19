package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class NilNode extends FqlNode
{

    public static NilNode instance = new NilNode();

    public NilNode()
    {
        super(row, col);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return null;
    }

    private boolean isClass()
    {
        return true;
    }
}
