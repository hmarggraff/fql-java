package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

import java.util.Iterator;

public class WhereClause implements FqlStatement
{
    private final FqlNodeInterface expr;

    public WhereClause(FqlNodeInterface expr)
    {
        this.expr = expr;
    }

    public Iterable execute(RunEnv env, Iterator precedent) throws FqlDataException
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    static class WhereIterator implements Iterator
    {

        public boolean hasNext()
        {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Object next()
        {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void remove()
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
