package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlIterator;
import org.fqlsource.data.RunEnv;

public class WhereClause implements FqlStatement
{
    private final FqlNodeInterface expr;

    public WhereClause(FqlNodeInterface expr)
    {
        this.expr = expr;
    }

    public FqlIterator execute(final RunEnv env, final FqlIterator precedent) throws FqlDataException
    {
        return new FqlIterator() {
            Object nextMatch;
            public boolean hasNext() throws FqlDataException
            {
                while (precedent.hasNext())
                {
                    nextMatch = precedent.next();
                    Object val = expr.getValue(env, nextMatch);
                    if (val instanceof Boolean)
                    {
                        Boolean cond = (Boolean) val;
                        if (cond)
                            return Boolean.TRUE;
                    }
                    else if (val == null)
                        continue;
                    else
                        return Boolean.TRUE;
                }
                nextMatch = null; // free it
                return false;
            }

            public Object next() throws FqlDataException
            {
                return nextMatch;
            }
        };
    }
}
