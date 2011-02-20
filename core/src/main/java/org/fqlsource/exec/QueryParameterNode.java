package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlQueryParameter;

/**
 */
public class QueryParameterNode extends FqlNode
{

    FqlQueryParameter param;

    public QueryParameterNode(FqlQueryParameter param, int row, int col)
    {
        super(row, col);
        this.param = param;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return env.getVariable(param);
    }

    public FqlQueryParameter getParam()
    {
        return param;
    }
}
