package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

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
        return booleanVal;
    }

    public boolean isBoolean()
    {
        return true;
    }

    public void dump(StringBuffer sb)
    {
        sb.append(booleanVal);
    }

}
