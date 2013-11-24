package org.funql.ri.util;

import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.NamedValues;

import java.util.List;

/**
 */
public class ListFqlIterator implements FqlIterator {
    protected final List data;
    protected int at;

    public ListFqlIterator(List data) {
	this.data = data;
    }


    @Override
    public NamedValues next() {
	if (at < data.size())
	    return new NamedValuesImpl("it", data.get(at++));
	return FqlIterator.sentinel;
    }
}
