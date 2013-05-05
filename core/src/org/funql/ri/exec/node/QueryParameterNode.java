package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.util.NamedIndex;

/**
 */
public class QueryParameterNode extends FqlNode
{

    NamedIndex param;

    public QueryParameterNode(NamedIndex param, int row, int col)
    {
        super(row, col);
        this.param = param;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return env.getVariable(param.getIndex());
    }

    public NamedIndex getParam()
    {
        return param;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "param");
    }

}
