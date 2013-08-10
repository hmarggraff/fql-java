package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

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

    public void dump(StringBuffer sb)
    {
        sb.append('"').append(stringVal).append('"');
    }

}
