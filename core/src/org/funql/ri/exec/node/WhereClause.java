package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.FqlStatement;

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
	    Object currentMatch;
            public boolean hasNext() throws FqlDataException
            {
		if (nextMatch != null)
		    return true;
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
		currentMatch = nextMatch;
		nextMatch = null;
		return currentMatch;
            }

	    @Override
	    public Object current()
	    {
		return currentMatch;
	    }
	};
    }
}
