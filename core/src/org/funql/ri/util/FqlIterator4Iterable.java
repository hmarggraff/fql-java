package org.funql.ri.util;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;

import java.util.Iterator;
import java.util.List;

/**
 */
public class FqlIterator4Iterable implements FqlIterator {
    protected Iterable data;
    protected Iterator it;
    protected int at;
    Object current;

    public FqlIterator4Iterable(Iterable data) {
        this.data = data;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public Object next() {
        if (!hasNext())
            throw new FqlDataException("Iterator beyond end: " + at);
        current = it.next();
        at++;
        return current;
    }

    @Override
    public Object current() {
        return current;
    }
}
