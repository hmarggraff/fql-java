package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

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

    public long getIntVal()
    {
        return intVal;
    }

    public void dump(StringBuffer sb)
    {
        sb.append(intVal);
    }

}
