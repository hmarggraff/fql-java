package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.FqlBuiltinFunction;

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
        Object[] argvals = new Object[argNodes!=null?argNodes.length:0];
        for (int i = 0; i < argvals.length; i++)
        {
            argvals[i] = argNodes[i].getValue(env, from);

        }
        return function.val(env, from, argvals);
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "function");
    }

    public FqlBuiltinFunction getFunction() {
        return function;
    }

    public FqlNodeInterface[] getArgNodes() {
        return argNodes;
    }

}
