package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.RunEnv;

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
