package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.RunEnv;
import org.fqlsource.util.NamedIndex;

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
