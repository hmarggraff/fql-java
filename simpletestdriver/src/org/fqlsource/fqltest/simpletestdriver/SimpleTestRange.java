package org.fqlsource.fqltest.simpletestdriver;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;

/**
 */
public class SimpleTestRange implements FqlIterator {
    private final int end;
    private int at;
    private final boolean includeEnd;

    public SimpleTestRange(int start, int end, boolean includeEnd) {
        at = start;
        this.end = end;
        this.includeEnd = includeEnd;
    }

    @Override
    public Object next() {
        if ((includeEnd ? at > end : at >= end))
            throw new FqlDataException("SimpleTestRange iterator beyond end: " + end);
        at++;
        return at-1;
    }

}
