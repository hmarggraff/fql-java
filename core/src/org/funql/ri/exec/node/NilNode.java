package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

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
