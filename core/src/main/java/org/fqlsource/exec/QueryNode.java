package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

import java.util.List;

public class QueryNode extends FqlNode
{
    List<FqlNodeInterface> clauses;

    public QueryNode(List<FqlNodeInterface> clauses, int row, int col)
    {
        super(row, col);
        this.clauses = clauses;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return null;
    }
}
