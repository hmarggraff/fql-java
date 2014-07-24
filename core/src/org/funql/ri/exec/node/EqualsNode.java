package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

/**
 */
public class EqualsNode extends BinaryNode
{
    public EqualsNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
    }



    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return realEqual(env, from);
    }

    @Override
    public String getOperator() {
        return "=";
    }
}
