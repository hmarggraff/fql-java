package org.funql.ri.util;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;

import java.util.Iterator;
import java.util.List;

/**
 */
public class FqlIterator4Iterable implements FqlIterator {
    protected final Iterator it;

    public FqlIterator4Iterable(Iterable data) {
        it = data.iterator();
    }

    @Override
    public Object next() {
        if (!it.hasNext())
            return sentinel;
        return it.next();
    }
}
