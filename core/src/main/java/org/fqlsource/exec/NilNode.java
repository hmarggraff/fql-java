package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class NilNode extends FqlNode
{

    public NilNode(int row, int col)
    {
        super(row, col);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return null;
    }

    public void dump(StringBuffer sb)
    {
        sb.append("nil");
    }


}
