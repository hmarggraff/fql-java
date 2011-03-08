package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

import java.util.Iterator;

public class WhereClause implements FqlStatementIterator
{
    private final FqlNodeInterface expr;

    public WhereClause(FqlNodeInterface expr)
    {
        this.expr = expr;
    }

    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException
    {
        return new DefaultFqlIterator(env, precedent, this);
    }

    public Object next(RunEnv env, Object parent) throws FqlDataException
    {
        Object val = expr.getValue(env, parent);
        if (val instanceof Boolean)
            return val;
        if (val == null)
            return Boolean.FALSE;
        else
            return Boolean.TRUE;
    }
}
