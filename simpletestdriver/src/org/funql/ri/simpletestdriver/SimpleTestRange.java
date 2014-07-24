package org.funql.ri.simpletestdriver;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.NamedValues;
import org.funql.ri.util.NamedValuesImpl;

/**
 */
public class SimpleTestRange implements FqlIterator {
    private final int end;
    private int at;
    private final boolean includeEnd;
    protected static String[] names = {"it"};

    public SimpleTestRange(int start, int end, boolean includeEnd) {
	at = start;
	this.end = end;
	this.includeEnd = includeEnd;
    }

    @Override
    public NamedValues next() {
	if ((includeEnd ? at > end : at >= end))
	    throw new FqlDataException("SimpleTestRange iterator beyond end: " + end);
	at++;
	return new NamedValuesImpl(names, new Object[]{at - 1});
    }

}
