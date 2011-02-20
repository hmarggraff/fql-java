package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class FunctionNode extends FqlNode
{
    private final FqlBuiltinFunction function;
    private final FqlNodeInterface[] argNodes;

    public FunctionNode(FqlBuiltinFunction function, FqlNodeInterface[] argNodes, int row, int col)
    {

        super(row, col);
        this.function = function;
        this.argNodes = argNodes;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object[] argvals = new Object[argNodes.length];
        for (int i = 0; i < argvals.length; i++)
        {
            argvals[i] = argNodes[i].getValue(env, from);

        }
        return function.val(env, from, argvals);
    }
}
