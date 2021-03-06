package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

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

    public boolean isFloat()
    {
        return true;
    }

    public void dump(StringBuffer sb)
    {
        sb.append(doubleVal);
    }

}
